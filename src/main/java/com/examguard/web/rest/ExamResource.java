package com.examguard.web.rest;

import com.examguard.domain.Exam;
import com.examguard.repository.ExamRepository;
import com.examguard.service.ExamService;
import com.examguard.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.examguard.domain.Exam}.
 */
@RestController
@RequestMapping("/api/exams")
public class ExamResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExamResource.class);

    private static final String ENTITY_NAME = "exam";

    @Value("${jhipster.clientApp.name:examguard}")
    private String applicationName;

    private final ExamService examService;

    private final ExamRepository examRepository;

    public ExamResource(ExamService examService, ExamRepository examRepository) {
        this.examService = examService;
        this.examRepository = examRepository;
    }

    /**
     * {@code POST  /exams} : Create a new exam.
     *
     * @param exam the exam to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new exam, or with status {@code 400 (Bad Request)} if the exam already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Exam> createExam(@Valid @RequestBody Exam exam) throws URISyntaxException {
        LOG.debug("REST request to save Exam : {}", exam);
        if (exam.getId() != null) {
            throw new BadRequestAlertException("A new exam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        exam = examService.save(exam);
        return ResponseEntity.created(new URI("/api/exams/" + exam.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, exam.getId().toString()))
            .body(exam);
    }

    /**
     * {@code PUT  /exams/:id} : Updates an existing exam.
     *
     * @param id the id of the exam to save.
     * @param exam the exam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exam,
     * or with status {@code 400 (Bad Request)} if the exam is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Exam exam)
        throws URISyntaxException {
        LOG.debug("REST request to update Exam : {}, {}", id, exam);
        if (exam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        exam = examService.update(exam);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exam.getId().toString()))
            .body(exam);
    }

    /**
     * {@code PATCH  /exams/:id} : Partial updates given fields of an existing exam, field will ignore if it is null
     *
     * @param id the id of the exam to save.
     * @param exam the exam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exam,
     * or with status {@code 400 (Bad Request)} if the exam is not valid,
     * or with status {@code 404 (Not Found)} if the exam is not found,
     * or with status {@code 500 (Internal Server Error)} if the exam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Exam> partialUpdateExam(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Exam exam
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Exam : {}, {}", id, exam);
        if (exam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Exam> result = examService.partialUpdate(exam);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exam.getId().toString())
        );
    }

    /**
     * {@code GET  /exams} : get all the Exams.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Exams in body.
     */
    @GetMapping("")
    public List<Exam> getAllExams() {
        LOG.debug("REST request to get all Exams");
        return examService.findAll();
    }

    /**
     * {@code GET  /exams/:id} : get the "id" exam.
     *
     * @param id the id of the exam to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exam, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Exam : {}", id);
        Optional<Exam> exam = examService.findOne(id);
        return ResponseUtil.wrapOrNotFound(exam);
    }

    /**
     * {@code DELETE  /exams/:id} : delete the "id" exam.
     *
     * @param id the id of the exam to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Exam : {}", id);
        examService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
