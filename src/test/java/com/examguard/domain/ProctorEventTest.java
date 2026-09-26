package com.examguard.domain;

import static com.examguard.domain.AttemptTestSamples.*;
import static com.examguard.domain.ProctorEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.examguard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProctorEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProctorEvent.class);
        ProctorEvent proctorEvent1 = getProctorEventSample1();
        ProctorEvent proctorEvent2 = new ProctorEvent();
        assertThat(proctorEvent1).isNotEqualTo(proctorEvent2);

        proctorEvent2.setId(proctorEvent1.getId());
        assertThat(proctorEvent1).isEqualTo(proctorEvent2);

        proctorEvent2 = getProctorEventSample2();
        assertThat(proctorEvent1).isNotEqualTo(proctorEvent2);
    }

    @Test
    void attemptTest() {
        ProctorEvent proctorEvent = getProctorEventRandomSampleGenerator();
        Attempt attemptBack = getAttemptRandomSampleGenerator();

        proctorEvent.setAttempt(attemptBack);
        assertThat(proctorEvent.getAttempt()).isEqualTo(attemptBack);

        proctorEvent.attempt(null);
        assertThat(proctorEvent.getAttempt()).isNull();
    }
}
