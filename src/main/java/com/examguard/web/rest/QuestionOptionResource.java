package com.examguard.web.rest;

import com.examguard.domain.QuestionOption;
import com.examguard.repository.QuestionOptionRepository;
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
 * REST controller for managing {@link com.examguard.domain.QuestionOption}.
 */
@RestController
@RequestMapping("/api/question-options")
@Transactional(rollbackFor = Exception.class)
public class QuestionOptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionOptionResource.class);

    private static final String ENTITY_NAME = "questionOption";

    @Value("${jhipster.clientApp.name:examguard}")
    private String applicationName;

    private final QuestionOptionRepository questionOptionRepository;

    public QuestionOptionResource(QuestionOptionRepository questionOptionRepository) {
        this.questionOptionRepository = questionOptionRepository;
    }

    /**
     * {@code POST  /question-options} : Create a new questionOption.
     *
     * @param questionOption the questionOption to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new questionOption, or with status {@code 400 (Bad Request)} if the questionOption already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<QuestionOption> createQuestionOption(@Valid @RequestBody QuestionOption questionOption)
        throws URISyntaxException {
        LOG.debug("REST request to save QuestionOption : {}", questionOption);
        if (questionOption.getId() != null) {
            throw new BadRequestAlertException("A new questionOption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        questionOption = questionOptionRepository.save(questionOption);
        return ResponseEntity.created(new URI("/api/question-options/" + questionOption.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, questionOption.getId().toString()))
            .body(questionOption);
    }

    /**
     * {@code PUT  /question-options/:id} : Updates an existing questionOption.
     *
     * @param id the id of the questionOption to save.
     * @param questionOption the questionOption to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionOption,
     * or with status {@code 400 (Bad Request)} if the questionOption is not valid,
     * or with status {@code 500 (Internal Server Error)} if the questionOption couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionOption> updateQuestionOption(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody QuestionOption questionOption
    ) throws URISyntaxException {
        LOG.debug("REST request to update QuestionOption : {}, {}", id, questionOption);
        if (questionOption.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionOption.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionOptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        questionOption = questionOptionRepository.save(questionOption);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, questionOption.getId().toString()))
            .body(questionOption);
    }

    /**
     * {@code PATCH  /question-options/:id} : Partial updates given fields of an existing questionOption, field will ignore if it is null
     *
     * @param id the id of the questionOption to save.
     * @param questionOption the questionOption to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated questionOption,
     * or with status {@code 400 (Bad Request)} if the questionOption is not valid,
     * or with status {@code 404 (Not Found)} if the questionOption is not found,
     * or with status {@code 500 (Internal Server Error)} if the questionOption couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<QuestionOption> partialUpdateQuestionOption(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody QuestionOption questionOption
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update QuestionOption : {}, {}", id, questionOption);
        if (questionOption.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, questionOption.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionOptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuestionOption> result = questionOptionRepository
            .findById(questionOption.getId())
            .map(existingQuestionOption -> {
                updateIfPresent(existingQuestionOption::setText, questionOption.getText());
                updateIfPresent(existingQuestionOption::setOrderIndex, questionOption.getOrderIndex());

                return existingQuestionOption;
            })
            .map(questionOptionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, questionOption.getId().toString())
        );
    }

    /**
     * {@code GET  /question-options} : get all the Question Options.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Question Options in body.
     */
    @GetMapping("")
    public List<QuestionOption> getAllQuestionOptions() {
        LOG.debug("REST request to get all QuestionOptions");
        return questionOptionRepository.findAll();
    }

    /**
     * {@code GET  /question-options/:id} : get the "id" questionOption.
     *
     * @param id the id of the questionOption to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the questionOption, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionOption> getQuestionOption(@PathVariable("id") Long id) {
        LOG.debug("REST request to get QuestionOption : {}", id);
        Optional<QuestionOption> questionOption = questionOptionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(questionOption);
    }

    /**
     * {@code DELETE  /question-options/:id} : delete the "id" questionOption.
     *
     * @param id the id of the questionOption to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionOption(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete QuestionOption : {}", id);
        questionOptionRepository.deleteById(id);
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
