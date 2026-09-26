package com.examguard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StudentAnswer.
 */
@Entity
@Table(name = "student_answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentAnswer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "selected_option_index", nullable = false)
    private Integer selectedOptionIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "questionOptions", "exam" }, allowSetters = true)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "studentAnswers", "proctorEvents", "student", "exam" }, allowSetters = true)
    private Attempt attempt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudentAnswer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSelectedOptionIndex() {
        return this.selectedOptionIndex;
    }

    public StudentAnswer selectedOptionIndex(Integer selectedOptionIndex) {
        this.setSelectedOptionIndex(selectedOptionIndex);
        return this;
    }

    public void setSelectedOptionIndex(Integer selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public StudentAnswer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public Attempt getAttempt() {
        return this.attempt;
    }

    public void setAttempt(Attempt attempt) {
        this.attempt = attempt;
    }

    public StudentAnswer attempt(Attempt attempt) {
        this.setAttempt(attempt);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentAnswer)) {
            return false;
        }
        return getId() != null && getId().equals(((StudentAnswer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentAnswer{" +
            "id=" + getId() +
            ", selectedOptionIndex=" + getSelectedOptionIndex() +
            "}";
    }
}
