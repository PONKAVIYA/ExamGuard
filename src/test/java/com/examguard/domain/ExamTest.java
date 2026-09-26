package com.examguard.domain;

import static com.examguard.domain.AttemptTestSamples.*;
import static com.examguard.domain.ExamTestSamples.*;
import static com.examguard.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.examguard.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ExamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Exam.class);
        Exam exam1 = getExamSample1();
        Exam exam2 = new Exam();
        assertThat(exam1).isNotEqualTo(exam2);

        exam2.setId(exam1.getId());
        assertThat(exam1).isEqualTo(exam2);

        exam2 = getExamSample2();
        assertThat(exam1).isNotEqualTo(exam2);
    }

    @Test
    void questionTest() {
        Exam exam = getExamRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        exam.addQuestion(questionBack);
        assertThat(exam.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getExam()).isEqualTo(exam);

        exam.removeQuestion(questionBack);
        assertThat(exam.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getExam()).isNull();

        exam.questions(new HashSet<>(Set.of(questionBack)));
        assertThat(exam.getQuestions()).containsOnly(questionBack);
        assertThat(questionBack.getExam()).isEqualTo(exam);

        exam.setQuestions(new HashSet<>());
        assertThat(exam.getQuestions()).doesNotContain(questionBack);
        assertThat(questionBack.getExam()).isNull();
    }

    @Test
    void attemptTest() {
        Exam exam = getExamRandomSampleGenerator();
        Attempt attemptBack = getAttemptRandomSampleGenerator();

        exam.addAttempt(attemptBack);
        assertThat(exam.getAttempts()).containsOnly(attemptBack);
        assertThat(attemptBack.getExam()).isEqualTo(exam);

        exam.removeAttempt(attemptBack);
        assertThat(exam.getAttempts()).doesNotContain(attemptBack);
        assertThat(attemptBack.getExam()).isNull();

        exam.attempts(new HashSet<>(Set.of(attemptBack)));
        assertThat(exam.getAttempts()).containsOnly(attemptBack);
        assertThat(attemptBack.getExam()).isEqualTo(exam);

        exam.setAttempts(new HashSet<>());
        assertThat(exam.getAttempts()).doesNotContain(attemptBack);
        assertThat(attemptBack.getExam()).isNull();
    }
}
