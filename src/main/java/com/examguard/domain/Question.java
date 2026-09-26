package com.examguard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "text", nullable = false)
    private String text;

    @NotNull
    @Column(name = "correct_option_index", nullable = false)
    private Integer correctOptionIndex;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "question" }, allowSetters = true)
    private Set<QuestionOption> questionOptions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "questions", "attempts" }, allowSetters = true)
    private Exam exam;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public Question text(String text) {
        this.setText(text);
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getCorrectOptionIndex() {
        return this.correctOptionIndex;
    }

    public Question correctOptionIndex(Integer correctOptionIndex) {
        this.setCorrectOptionIndex(correctOptionIndex);
        return this;
    }

    public void setCorrectOptionIndex(Integer correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public Set<QuestionOption> getQuestionOptions() {
        return this.questionOptions;
    }

    public void setQuestionOptions(Set<QuestionOption> questionOptions) {
        if (this.questionOptions != null) {
            this.questionOptions.forEach(i -> i.setQuestion(null));
        }
        if (questionOptions != null) {
            questionOptions.forEach(i -> i.setQuestion(this));
        }
        this.questionOptions = questionOptions;
    }

    public Question questionOptions(Set<QuestionOption> questionOptions) {
        this.setQuestionOptions(questionOptions);
        return this;
    }

    public Question addQuestionOption(QuestionOption questionOption) {
        this.questionOptions.add(questionOption);
        questionOption.setQuestion(this);
        return this;
    }

    public Question removeQuestionOption(QuestionOption questionOption) {
        this.questionOptions.remove(questionOption);
        questionOption.setQuestion(null);
        return this;
    }

    public Exam getExam() {
        return this.exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Question exam(Exam exam) {
        this.setExam(exam);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", text='" + getText() + "'" +
            ", correctOptionIndex=" + getCorrectOptionIndex() +
            "}";
    }
}
