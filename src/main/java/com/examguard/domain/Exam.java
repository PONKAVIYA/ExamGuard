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
 * A Exam.
 */
@Entity
@Table(name = "exam")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Exam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "active")
    private Boolean active;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exam")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "questionOptions", "exam" }, allowSetters = true)
    private Set<Question> questions = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exam")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "studentAnswers", "proctorEvents", "student", "exam" }, allowSetters = true)
    private Set<Attempt> attempts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Exam title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDurationSeconds() {
        return this.durationSeconds;
    }

    public Exam durationSeconds(Integer durationSeconds) {
        this.setDurationSeconds(durationSeconds);
        return this;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Exam active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Set<Question> questions) {
        if (this.questions != null) {
            this.questions.forEach(i -> i.setExam(null));
        }
        if (questions != null) {
            questions.forEach(i -> i.setExam(this));
        }
        this.questions = questions;
    }

    public Exam questions(Set<Question> questions) {
        this.setQuestions(questions);
        return this;
    }

    public Exam addQuestion(Question question) {
        this.questions.add(question);
        question.setExam(this);
        return this;
    }

    public Exam removeQuestion(Question question) {
        this.questions.remove(question);
        question.setExam(null);
        return this;
    }

    public Set<Attempt> getAttempts() {
        return this.attempts;
    }

    public void setAttempts(Set<Attempt> attempts) {
        if (this.attempts != null) {
            this.attempts.forEach(i -> i.setExam(null));
        }
        if (attempts != null) {
            attempts.forEach(i -> i.setExam(this));
        }
        this.attempts = attempts;
    }

    public Exam attempts(Set<Attempt> attempts) {
        this.setAttempts(attempts);
        return this;
    }

    public Exam addAttempt(Attempt attempt) {
        this.attempts.add(attempt);
        attempt.setExam(this);
        return this;
    }

    public Exam removeAttempt(Attempt attempt) {
        this.attempts.remove(attempt);
        attempt.setExam(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        return getId() != null && getId().equals(((Exam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exam{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", durationSeconds=" + getDurationSeconds() +
            ", active='" + getActive() + "'" +
            "}";
    }
}
