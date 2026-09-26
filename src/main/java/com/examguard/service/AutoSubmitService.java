package com.examguard.service;

import com.examguard.domain.Attempt;
import com.examguard.domain.StudentAnswer;
import com.examguard.repository.AttemptRepository;
import com.examguard.repository.StudentAnswerRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Syllabus link (Unit I): multithreading / synchronization.
 *
 * Spring's @Scheduled runs this method on its own background thread pool,
 * completely separate from the threads handling web requests. Every 10
 * seconds it wakes up, finds any attempt whose server-side endedAt has
 * passed but that the student never manually submitted, and grades it
 * automatically.
 *
 * This is the concrete proof, in your viva, that "the server decides when
 * time is up" isn't just a sentence in the report — it's this method
 * actually enforcing it independently of anything the student's browser
 * does or doesn't do (closing the tab, changing the system clock, etc.)
 */
@Service
public class AutoSubmitService {

    private static final Logger log = LoggerFactory.getLogger(AutoSubmitService.class);

    private final AttemptRepository attemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Autowired
    public AutoSubmitService(AttemptRepository attemptRepository, StudentAnswerRepository studentAnswerRepository) {
        this.attemptRepository = attemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
    }

    // Runs every 10,000 ms (10 seconds). Fine-grained enough for a demo,
    // light enough not to hammer the database.
    @Scheduled(fixedRate = 10000)
    public void autoSubmitExpiredAttempts() {
        Instant now = Instant.now();

        List<Attempt> expiredButOpen = attemptRepository
            .findAll()
            .stream()
            // defensive: skip malformed rows (e.g. created via the generic
            // CRUD screen without a student) instead of crashing the
            // scheduler for every other valid attempt too
            .filter(a -> a.getStudent() != null)
            .filter(a -> !a.getSubmitted() && a.getEndedAt().isBefore(now))
            .collect(Collectors.toList());

        for (Attempt attempt : expiredButOpen) {
            int score = gradeAttempt(attempt);
            attempt.setSubmitted(true);
            attempt.setScore(score);
            attemptRepository.save(attempt);
            log.info("Auto-submitted expired attempt {} for student {} with score {}",
                attempt.getId(), attempt.getStudent().getLogin(), score);
        }
    }

    // Same grading rule as the manual submit endpoint — kept identical on
    // purpose so a manually-submitted and an auto-submitted attempt are
    // scored exactly the same way.
    private int gradeAttempt(Attempt attempt) {
        List<StudentAnswer> answers = studentAnswerRepository
            .findAll()
            .stream()
            .filter(a -> a.getAttempt() != null && a.getAttempt().getId().equals(attempt.getId()))
            .collect(Collectors.toList());

        int score = 0;
        for (StudentAnswer answer : answers) {
            if (answer.getQuestion() != null && answer.getSelectedOptionIndex() == answer.getQuestion().getCorrectOptionIndex()) {
                score++;
            }
        }
        return score;
    }
}