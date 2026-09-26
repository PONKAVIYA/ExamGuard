package com.examguard.domain;

import static com.examguard.domain.QuestionOptionTestSamples.*;
import static com.examguard.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.examguard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuestionOptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuestionOption.class);
        QuestionOption questionOption1 = getQuestionOptionSample1();
        QuestionOption questionOption2 = new QuestionOption();
        assertThat(questionOption1).isNotEqualTo(questionOption2);

        questionOption2.setId(questionOption1.getId());
        assertThat(questionOption1).isEqualTo(questionOption2);

        questionOption2 = getQuestionOptionSample2();
        assertThat(questionOption1).isNotEqualTo(questionOption2);
    }

    @Test
    void questionTest() {
        QuestionOption questionOption = getQuestionOptionRandomSampleGenerator();
        Question questionBack = getQuestionRandomSampleGenerator();

        questionOption.setQuestion(questionBack);
        assertThat(questionOption.getQuestion()).isEqualTo(questionBack);

        questionOption.question(null);
        assertThat(questionOption.getQuestion()).isNull();
    }
}
