
package com.examguard.web.rest;

import com.examguard.domain.*;
import com.examguard.repository.*;
import com.examguard.security.SecurityUtils;
import com.examguard.service.dto.ExamTakingDTOs.*;
import com.examguard.web.rest.errors.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExamTakingResource {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final ProctorEventRepository proctorEventRepository;
    private final UserRepository userRepository;

    @Autowired
    public ExamTakingResource(
        ExamRepository examRepository,
        QuestionRepository questionRepository,
        AttemptRepository attemptRepository,
        StudentAnswerRepository studentAnswerRepository,
        ProctorEventRepository proctorEventRepository,
        UserRepository userRepository
    ) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.proctorEventRepository = proctorEventRepository;
        this.userRepository = userRepository;
    }

    // =====================================================================
    // Current logged-in user
    // =====================================================================

    private User currentUser() {
        String login = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() ->
                new UnauthorizedAccessException("Not logged in")
            );

        return userRepository.findOneByLogin(login)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "User not found: " + login
                )
            );
    }

    // =====================================================================
    // Start exam
    // POST /api/exams/{examId}/attempts
    // =====================================================================

    @Transactional
    @PostMapping("/exams/{examId}/attempts")
    public ResponseEntity<StartAttemptResponse> startAttempt(
        @PathVariable Long examId
    ) {
        User student = currentUser();

        Exam exam = examRepository.findById(examId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Exam not found: " + examId
                )
            );

        /*
         * Look for an existing attempt for this student and exam.
         *
         * If the attempt is already submitted, do not allow another attempt.
         *
         * If the attempt is still in progress, reuse it.
         * This prevents refreshing the page from creating a new timer.
         */
        Attempt existingAttempt = attemptRepository
            .findAll()
            .stream()
            .filter(a ->
                a.getExam() != null &&
                a.getStudent() != null &&
                a.getExam().getId().equals(examId) &&
                a.getStudent().getId().equals(student.getId())
            )
            .findFirst()
            .orElse(null);

        Attempt attempt;

        if (existingAttempt != null) {
            if (existingAttempt.getSubmitted()) {
                throw new DuplicateAttemptException(
                    "You have already attempted this exam."
                );
            }

            attempt = existingAttempt;
        } else {
            Instant now = Instant.now();
            Instant endsAt = now.plusSeconds(
                exam.getDurationSeconds()
            );

            attempt = new Attempt();
            attempt.setExam(exam);
            attempt.setStudent(student);
            attempt.setStartedAt(now);
            attempt.setEndedAt(endsAt);
            attempt.setSubmitted(false);

            attempt = attemptRepository.save(attempt);
        }

        /*
         * Send questions to the student.
         * correctOptionIndex is NOT included.
         */
        List<QuestionForStudent> questions = questionRepository
            .findAll()
            .stream()
            .filter(q ->
                q.getExam() != null &&
                q.getExam().getId().equals(examId)
            )
            .map(q ->
                new QuestionForStudent(
                    q.getId(),
                    q.getText(),
                    q.getQuestionOptions()
                        .stream()
                        .sorted(
                            Comparator.comparingInt(
                                QuestionOption::getOrderIndex
                            )
                        )
                        .map(o ->
                            new OptionForStudent(
                                o.getId(),
                                o.getText(),
                                o.getOrderIndex()
                            )
                        )
                        .collect(Collectors.toList())
                )
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(
            new StartAttemptResponse(
                attempt.getId(),
                attempt.getEndedAt(),
                questions
            )
        );
    }

    // =====================================================================
    // Save answer
    // POST /api/attempts/{attemptId}/answers
    // =====================================================================

    @Transactional
    @PostMapping("/attempts/{attemptId}/answers")
    public ResponseEntity<SaveAnswerResponse> saveAnswer(
        @PathVariable Long attemptId,
        @RequestBody SaveAnswerRequest request
    ) {
        Attempt attempt = loadOwnAttempt(attemptId);

        // Exam time expired
        if (Instant.now().isAfter(attempt.getEndedAt())) {
            throw new ExamExpiredException(
                "This exam has already ended."
            );
        }

        // Already submitted
        if (attempt.getSubmitted()) {
            throw new ExamExpiredException(
                "This attempt is already submitted."
            );
        }

        Question question = questionRepository.findById(
            request.questionId()
        ).orElseThrow(() ->
            new ResourceNotFoundException(
                "Question not found"
            )
        );

        /*
         * Find existing answer for this question.
         * Update it when the student changes the answer.
         */
        StudentAnswer answer = studentAnswerRepository
            .findAll()
            .stream()
            .filter(a ->
                a.getAttempt() != null &&
                a.getQuestion() != null &&
                a.getAttempt().getId().equals(attemptId) &&
                a.getQuestion().getId().equals(question.getId())
            )
            .findFirst()
            .orElseGet(() -> {
                StudentAnswer newAnswer = new StudentAnswer();
                newAnswer.setAttempt(attempt);
                newAnswer.setQuestion(question);
                return newAnswer;
            });

        answer.setSelectedOptionIndex(
            request.selectedOptionIndex()
        );

        studentAnswerRepository.save(answer);

        return ResponseEntity.ok(
            new SaveAnswerResponse(
                true,
                Instant.now()
            )
        );
    }

    // =====================================================================
    // Submit exam
    // POST /api/attempts/{attemptId}/submit
    // =====================================================================

    /*
     * IMPORTANT:
     * The transaction keeps the Hibernate session open while grading.
     */
    @Transactional
    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<SubmitResponse> submit(
        @PathVariable Long attemptId
    ) {
        Attempt attempt = loadOwnAttempt(attemptId);

        if (attempt.getSubmitted()) {
            throw new ExamExpiredException(
                "This attempt is already submitted."
            );
        }

        int score = gradeAttempt(attempt);

        attempt.setSubmitted(true);
        attempt.setScore(score);

        attemptRepository.save(attempt);

        int total = (int) questionRepository
            .findAll()
            .stream()
            .filter(q ->
                q.getExam() != null &&
                attempt.getExam() != null &&
                q.getExam().getId().equals(
                    attempt.getExam().getId()
                )
            )
            .count();

        return ResponseEntity.ok(
            new SubmitResponse(
                score,
                total
            )
        );
    }

    // =====================================================================
    // Grade exam
    // =====================================================================

    /*
     * Explicitly load the Question from QuestionRepository.
     *
     * This avoids depending on:
     *
     * answer.getQuestion().getCorrectOptionIndex()
     *
     * which was causing:
     *
     * Could not initialize proxy Question#1500 - no session
     */
    private int gradeAttempt(Attempt attempt) {

        List<StudentAnswer> answers = studentAnswerRepository
            .findAll()
            .stream()
            .filter(a ->
                a.getAttempt() != null &&
                a.getAttempt().getId().equals(
                    attempt.getId()
                )
            )
            .collect(Collectors.toList());

        int score = 0;

        for (StudentAnswer answer : answers) {

            if (answer.getQuestion() == null) {
                continue;
            }

            Long questionId = answer.getQuestion().getId();

            Question question = questionRepository
                .findById(questionId)
                .orElse(null);

            if (question == null) {
                continue;
            }

            if (
                answer.getSelectedOptionIndex() ==
                question.getCorrectOptionIndex()
            ) {
                score++;
            }
        }

        return score;
    }

    // =====================================================================
    // Review
    // GET /api/attempts/{attemptId}/review
    // =====================================================================

    @Transactional
    @GetMapping("/attempts/{attemptId}/review")
    public ResponseEntity<List<ReviewItem>> review(
        @PathVariable Long attemptId
    ) {
        Attempt attempt = loadOwnAttempt(attemptId);

        if (!attempt.getSubmitted()) {
            throw new ExamExpiredException(
                "Submit the exam before viewing the review."
            );
        }

        List<Question> questions = questionRepository
            .findAll()
            .stream()
            .filter(q ->
                q.getExam() != null &&
                attempt.getExam() != null &&
                q.getExam().getId().equals(
                    attempt.getExam().getId()
                )
            )
            .collect(Collectors.toList());

        List<StudentAnswer> answers = studentAnswerRepository
            .findAll()
            .stream()
            .filter(a ->
                a.getAttempt() != null &&
                a.getAttempt().getId().equals(attemptId)
            )
            .collect(Collectors.toList());

        List<ReviewItem> items = questions
            .stream()
            .map(q -> {

                List<String> optionTexts = q
                    .getQuestionOptions()
                    .stream()
                    .sorted(
                        Comparator.comparingInt(
                            QuestionOption::getOrderIndex
                        )
                    )
                    .map(QuestionOption::getText)
                    .collect(Collectors.toList());

                Integer selected = answers
                    .stream()
                    .filter(a ->
                        a.getQuestion() != null &&
                        a.getQuestion()
                            .getId()
                            .equals(q.getId())
                    )
                    .map(
                        StudentAnswer::getSelectedOptionIndex
                    )
                    .findFirst()
                    .orElse(null);

                return new ReviewItem(
                    q.getText(),
                    optionTexts,
                    q.getCorrectOptionIndex(),
                    selected
                );
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

    // =====================================================================
    // Proctoring event
    // POST /api/attempts/{attemptId}/events
    // =====================================================================

    @Transactional
    @PostMapping("/attempts/{attemptId}/events")
    public ResponseEntity<ProctorEventResponse> recordEvent(
        @PathVariable Long attemptId,
        @RequestBody ProctorEventRequest request
    ) {
        Attempt attempt = loadOwnAttempt(attemptId);

        ProctorEvent event = new ProctorEvent();
        event.setAttempt(attempt);
        event.setEventType(request.type());
        event.setTimestamp(Instant.now());

        proctorEventRepository.save(event);

        return ResponseEntity.ok(
            new ProctorEventResponse(true)
        );
    }

    // =====================================================================
    // Live teacher dashboard
    // GET /api/exams/{examId}/live
    // =====================================================================

    @Transactional
    @GetMapping("/exams/{examId}/live")
    public ResponseEntity<List<LiveStudentStatus>> live(
        @PathVariable Long examId
    ) {
        examRepository.findById(examId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Exam not found: " + examId
                )
            );

        int totalQuestions = (int) questionRepository
            .findAll()
            .stream()
            .filter(q ->
                q.getExam() != null &&
                q.getExam().getId().equals(examId)
            )
            .count();

        List<Attempt> attempts = attemptRepository
            .findAll()
            .stream()
            .filter(a ->
                a.getExam() != null &&
                a.getExam().getId().equals(examId)
            )
            .collect(Collectors.toList());

        List<LiveStudentStatus> result = attempts
            .stream()
            .map(a -> {

                int answered = (int) studentAnswerRepository
                    .findAll()
                    .stream()
                    .filter(ans ->
                        ans.getAttempt() != null &&
                        ans.getAttempt().getId().equals(a.getId())
                    )
                    .count();

                int tabSwitches = (int) proctorEventRepository
                    .findAll()
                    .stream()
                    .filter(ev ->
                        ev.getAttempt() != null &&
                        ev.getAttempt().getId().equals(a.getId()) &&
                        "TAB_SWITCH".equals(
                            ev.getEventType()
                        )
                    )
                    .count();

                return new LiveStudentStatus(
                    a.getStudent().getLogin(),
                    a.getSubmitted()
                        ? "SUBMITTED"
                        : "IN_PROGRESS",
                    answered,
                    totalQuestions,
                    tabSwitches,
                    a.getScore()
                );
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // =====================================================================
    // Load own attempt
    // =====================================================================

    private Attempt loadOwnAttempt(Long attemptId) {

        Attempt attempt = attemptRepository
            .findById(attemptId)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Attempt not found: " + attemptId
                )
            );

        User me = currentUser();

        if (
            attempt.getStudent() == null ||
            !attempt.getStudent()
                .getId()
                .equals(me.getId())
        ) {
            throw new UnauthorizedAccessException(
                "This attempt does not belong to you."
            );
        }

        return attempt;
    }
}