package com.examguard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Attempt.
 */
@Entity
@Table(name = "attempt")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Attempt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @NotNull
    @Column(name = "ended_at", nullable = false)
    private Instant endedAt;

    @Column(name = "submitted")
    private Boolean submitted;

    @Column(name = "score")
    private Integer score;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attempt")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "question", "attempt" }, allowSetters = true)
    private Set<StudentAnswer> studentAnswers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attempt")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "attempt" }, allowSetters = true)
    private Set<ProctorEvent> proctorEvents = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "questions", "attempts" }, allowSetters = true)
    private Exam exam;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Attempt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public Attempt startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return this.endedAt;
    }

    public Attempt endedAt(Instant endedAt) {
        this.setEndedAt(endedAt);
        return this;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public Boolean getSubmitted() {
        return this.submitted;
    }

    public Attempt submitted(Boolean submitted) {
        this.setSubmitted(submitted);
        return this;
    }

    public void setSubmitted(Boolean submitted) {
        this.submitted = submitted;
    }

    public Integer getScore() {
        return this.score;
    }

    public Attempt score(Integer score) {
        this.setScore(score);
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Set<StudentAnswer> getStudentAnswers() {
        return this.studentAnswers;
    }

    public void setStudentAnswers(Set<StudentAnswer> studentAnswers) {
        if (this.studentAnswers != null) {
            this.studentAnswers.forEach(i -> i.setAttempt(null));
        }
        if (studentAnswers != null) {
            studentAnswers.forEach(i -> i.setAttempt(this));
        }
        this.studentAnswers = studentAnswers;
    }

    public Attempt studentAnswers(Set<StudentAnswer> studentAnswers) {
        this.setStudentAnswers(studentAnswers);
        return this;
    }

    public Attempt addStudentAnswer(StudentAnswer studentAnswer) {
        this.studentAnswers.add(studentAnswer);
        studentAnswer.setAttempt(this);
        return this;
    }

    public Attempt removeStudentAnswer(StudentAnswer studentAnswer) {
        this.studentAnswers.remove(studentAnswer);
        studentAnswer.setAttempt(null);
        return this;
    }

    public Set<ProctorEvent> getProctorEvents() {
        return this.proctorEvents;
    }

    public void setProctorEvents(Set<ProctorEvent> proctorEvents) {
        if (this.proctorEvents != null) {
            this.proctorEvents.forEach(i -> i.setAttempt(null));
        }
        if (proctorEvents != null) {
            proctorEvents.forEach(i -> i.setAttempt(this));
        }
        this.proctorEvents = proctorEvents;
    }

    public Attempt proctorEvents(Set<ProctorEvent> proctorEvents) {
        this.setProctorEvents(proctorEvents);
        return this;
    }

    public Attempt addProctorEvent(ProctorEvent proctorEvent) {
        this.proctorEvents.add(proctorEvent);
        proctorEvent.setAttempt(this);
        return this;
    }

    public Attempt removeProctorEvent(ProctorEvent proctorEvent) {
        this.proctorEvents.remove(proctorEvent);
        proctorEvent.setAttempt(null);
        return this;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public Attempt student(User user) {
        this.setStudent(user);
        return this;
    }

    public Exam getExam() {
        return this.exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Attempt exam(Exam exam) {
        this.setExam(exam);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Attempt)) {
            return false;
        }
        return getId() != null && getId().equals(((Attempt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Attempt{" +
            "id=" + getId() +
            ", startedAt='" + getStartedAt() + "'" +
            ", endedAt='" + getEndedAt() + "'" +
            ", submitted='" + getSubmitted() + "'" +
            ", score=" + getScore() +
            "}";
    }
}
