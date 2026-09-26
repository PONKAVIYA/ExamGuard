package com.examguard.domain;

import static com.examguard.domain.AttemptTestSamples.*;
import static com.examguard.domain.ExamTestSamples.*;
import static com.examguard.domain.ProctorEventTestSamples.*;
import static com.examguard.domain.StudentAnswerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.examguard.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AttemptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Attempt.class);
        Attempt attempt1 = getAttemptSample1();
        Attempt attempt2 = new Attempt();
        assertThat(attempt1).isNotEqualTo(attempt2);

        attempt2.setId(attempt1.getId());
        assertThat(attempt1).isEqualTo(attempt2);

        attempt2 = getAttemptSample2();
        assertThat(attempt1).isNotEqualTo(attempt2);
    }

    @Test
    void studentAnswerTest() {
        Attempt attempt = getAttemptRandomSampleGenerator();
        StudentAnswer studentAnswerBack = getStudentAnswerRandomSampleGenerator();

        attempt.addStudentAnswer(studentAnswerBack);
        assertThat(attempt.getStudentAnswers()).containsOnly(studentAnswerBack);
        assertThat(studentAnswerBack.getAttempt()).isEqualTo(attempt);

        attempt.removeStudentAnswer(studentAnswerBack);
        assertThat(attempt.getStudentAnswers()).doesNotContain(studentAnswerBack);
        assertThat(studentAnswerBack.getAttempt()).isNull();

        attempt.studentAnswers(new HashSet<>(Set.of(studentAnswerBack)));
        assertThat(attempt.getStudentAnswers()).containsOnly(studentAnswerBack);
        assertThat(studentAnswerBack.getAttempt()).isEqualTo(attempt);

        attempt.setStudentAnswers(new HashSet<>());
        assertThat(attempt.getStudentAnswers()).doesNotContain(studentAnswerBack);
        assertThat(studentAnswerBack.getAttempt()).isNull();
    }

    @Test
    void proctorEventTest() {
        Attempt attempt = getAttemptRandomSampleGenerator();
        ProctorEvent proctorEventBack = getProctorEventRandomSampleGenerator();

        attempt.addProctorEvent(proctorEventBack);
        assertThat(attempt.getProctorEvents()).containsOnly(proctorEventBack);
        assertThat(proctorEventBack.getAttempt()).isEqualTo(attempt);

        attempt.removeProctorEvent(proctorEventBack);
        assertThat(attempt.getProctorEvents()).doesNotContain(proctorEventBack);
        assertThat(proctorEventBack.getAttempt()).isNull();

        attempt.proctorEvents(new HashSet<>(Set.of(proctorEventBack)));
        assertThat(attempt.getProctorEvents()).containsOnly(proctorEventBack);
        assertThat(proctorEventBack.getAttempt()).isEqualTo(attempt);

        attempt.setProctorEvents(new HashSet<>());
        assertThat(attempt.getProctorEvents()).doesNotContain(proctorEventBack);
        assertThat(proctorEventBack.getAttempt()).isNull();
    }

    @Test
    void examTest() {
        Attempt attempt = getAttemptRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        attempt.setExam(examBack);
        assertThat(attempt.getExam()).isEqualTo(examBack);

        attempt.exam(null);
        assertThat(attempt.getExam()).isNull();
    }
}
