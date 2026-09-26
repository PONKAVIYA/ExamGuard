package com.examguard.domain;

import static com.examguard.domain.AttemptTestSamples.*;
import static com.examguard.domain.QuestionTestSamples.*;
import static com.examguard.domain.StudentAnswerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.examguard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentAnswerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StudentAnswer.class);
        StudentAnswer studentAnswer1 = getStudentAnswerSample1();
        StudentAnswer studentAnswer2 = new StudentAnswer();
        assertThat(studentAnswer1).isNotEqualTo(studentAnswer2);

        studentAnswer2.setId(studentAnswer1.getId());
        assertThat(studentAnswer1).isEqualTo(studentAnswer2);

        studentAnswer2 = getStudentAnswerSample2();
        assertThat(studentAnswer1).isNotEqualTo(studentAnswer2);
    }

    @Test
    void questionTest() {
        StudentAnswer studentAnswer = getStudentAnswerRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        studentAnswer.setQuestion(questionBack);
        assertThat(studentAnswer.getQuestion()).isEqualTo(questionBack);

        studentAnswer.question(null);
        assertThat(studentAnswer.getQuestion()).isNull();
    }

    @Test
    void attemptTest() {
        StudentAnswer studentAnswer = getStudentAnswerRandomSampleGenerator();
        Attempt attemptBack = getAttemptRandomSampleGenerator();

        studentAnswer.setAttempt(attemptBack);
        assertThat(studentAnswer.getAttempt()).isEqualTo(attemptBack);

        studentAnswer.attempt(null);
        assertThat(studentAnswer.getAttempt()).isNull();
    }
}
