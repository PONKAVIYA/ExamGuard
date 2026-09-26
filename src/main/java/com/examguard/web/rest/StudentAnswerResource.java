package com.examguard.web.rest;

import com.examguard.domain.StudentAnswer;
import com.examguard.repository.StudentAnswerRepository;
import com.examguard.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.examguard.domain.StudentAnswer}.
 */
@RestController
@RequestMapping("/api/student-answers")
@Transactional(rollbackFor = Exception.class)
public class StudentAnswerResource {

    private static final Logger LOG = LoggerFactory.getLogger(StudentAnswerResource.class);

    private static final String ENTITY_NAME = "studentAnswer";

    @Value("${jhipster.clientApp.name:examguard}")
    private String applicationName;

    private final StudentAnswerRepository studentAnswerRepository;

    public StudentAnswerResource(StudentAnswerRepository studentAnswerRepository) {
        this.studentAnswerRepository = studentAnswerRepository;
    }

    /**
     * {@code POST  /student-answers} : Create a new studentAnswer.
     *
     * @param studentAnswer the studentAnswer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new studentAnswer, or with status {@code 400 (Bad Request)} if the studentAnswer already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StudentAnswer> createStudentAnswer(@Valid @RequestBody StudentAnswer studentAnswer) throws URISyntaxException {
        LOG.debug("REST request to save StudentAnswer : {}", studentAnswer);
        if (studentAnswer.getId() != null) {
            throw new BadRequestAlertException("A new studentAnswer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        studentAnswer = studentAnswerRepository.save(studentAnswer);
        return ResponseEntity.created(new URI("/api/student-answers/" + studentAnswer.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, studentAnswer.getId().toString()))
            .body(studentAnswer);
    }

    /**
     * {@code PUT  /student-answers/:id} : Updates an existing studentAnswer.
     *
     * @param id the id of the studentAnswer to save.
     * @param studentAnswer the studentAnswer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentAnswer,
     * or with status {@code 400 (Bad Request)} if the studentAnswer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the studentAnswer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentAnswer> updateStudentAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StudentAnswer studentAnswer
    ) throws URISyntaxException {
        LOG.debug("REST request to update StudentAnswer : {}, {}", id, studentAnswer);
        if (studentAnswer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentAnswer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        studentAnswer = studentAnswerRepository.save(studentAnswer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentAnswer.getId().toString()))
            .body(studentAnswer);
    }

    /**
     * {@code PATCH  /student-answers/:id} : Partial updates given fields of an existing studentAnswer, field will ignore if it is null
     *
     * @param id the id of the studentAnswer to save.
     * @param studentAnswer the studentAnswer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated studentAnswer,
     * or with status {@code 400 (Bad Request)} if the studentAnswer is not valid,
     * or with status {@code 404 (Not Found)} if the studentAnswer is not found,
     * or with status {@code 500 (Internal Server Error)} if the studentAnswer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StudentAnswer> partialUpdateStudentAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StudentAnswer studentAnswer
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update StudentAnswer : {}, {}", id, studentAnswer);
        if (studentAnswer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, studentAnswer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentAnswerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StudentAnswer> result = studentAnswerRepository
            .findById(studentAnswer.getId())
            .map(existingStudentAnswer -> {
                updateIfPresent(existingStudentAnswer::setSelectedOptionIndex, studentAnswer.getSelectedOptionIndex());

                return existingStudentAnswer;
            })
            .map(studentAnswerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, studentAnswer.getId().toString())
        );
    }

    /**
     * {@code GET  /student-answers} : get all the Student Answers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Student Answers in body.
     */
    @GetMapping("")
    public List<StudentAnswer> getAllStudentAnswers() {
        LOG.debug("REST request to get all StudentAnswers");
        return studentAnswerRepository.findAll();
    }

    /**
     * {@code GET  /student-answers/:id} : get the "id" studentAnswer.
     *
     * @param id the id of the studentAnswer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the studentAnswer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentAnswer> getStudentAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StudentAnswer : {}", id);
        Optional<StudentAnswer> studentAnswer = studentAnswerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(studentAnswer);
    }

    /**
     * {@code DELETE  /student-answers/:id} : delete the "id" studentAnswer.
     *
     * @param id the id of the studentAnswer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudentAnswer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StudentAnswer : {}", id);
        studentAnswerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
