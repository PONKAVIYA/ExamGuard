package com.examguard.domain;

import static com.examguard.domain.ExamTestSamples.*;
import static com.examguard.domain.QuestionOptionTestSamples.*;
import static com.examguard.domain.QuestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.examguard.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Question.class);
        Question question1 = getQuestionSample1();
        Question question2 = new Question();
        assertThat(question1).isNotEqualTo(question2);

        question2.setId(question1.getId());
        assertThat(question1).isEqualTo(question2);

        question2 = getQuestionSample2();
        assertThat(question1).isNotEqualTo(question2);
    }

    @Test
    void questionOptionTest() {
        Question question = getQuestionRandomSampleGenerator();
        QuestionOption questionOptionBack = getQuestionOptionRandomSampleGenerator();

        question.addQuestionOption(questionOptionBack);
        assertThat(question.getQuestionOptions()).containsOnly(questionOptionBack);
        assertThat(questionOptionBack.getQuestion()).isEqualTo(question);

        question.removeQuestionOption(questionOptionBack);
        assertThat(question.getQuestionOptions()).doesNotContain(questionOptionBack);
        assertThat(questionOptionBack.getQuestion()).isNull();

        question.questionOptions(new HashSet<>(Set.of(questionOptionBack)));
        assertThat(question.getQuestionOptions()).containsOnly(questionOptionBack);
        assertThat(questionOptionBack.getQuestion()).isEqualTo(question);

        question.setQuestionOptions(new HashSet<>());
        assertThat(question.getQuestionOptions()).doesNotContain(questionOptionBack);
        assertThat(questionOptionBack.getQuestion()).isNull();
    }

    @Test
    void examTest() {
        Question question = getQuestionRandomSampleGenerator();
        Exam examBack = getExamRandomSampleGenerator();

        question.setExam(examBack);
        assertThat(question.getExam()).isEqualTo(examBack);

        question.exam(null);
        assertThat(question.getExam()).isNull();
    }
}
