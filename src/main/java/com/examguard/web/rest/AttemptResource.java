package com.examguard.web.rest;

import com.examguard.domain.Attempt;
import com.examguard.repository.AttemptRepository;
import com.examguard.service.AttemptService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.examguard.domain.Attempt}.
 */
@RestController
@RequestMapping("/api/attempts")
public class AttemptResource {

    private static final Logger LOG = LoggerFactory.getLogger(AttemptResource.class);

    private static final String ENTITY_NAME = "attempt";

    @Value("${jhipster.clientApp.name:examguard}")
    private String applicationName;

    private final AttemptService attemptService;

    private final AttemptRepository attemptRepository;

    public AttemptResource(AttemptService attemptService, AttemptRepository attemptRepository) {
        this.attemptService = attemptService;
        this.attemptRepository = attemptRepository;
    }

    /**
     * {@code POST  /attempts} : Create a new attempt.
     *
     * @param attempt the attempt to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attempt, or with status {@code 400 (Bad Request)} if the attempt already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Attempt> createAttempt(@Valid @RequestBody Attempt attempt) throws URISyntaxException {
        LOG.debug("REST request to save Attempt : {}", attempt);
        if (attempt.getId() != null) {
            throw new BadRequestAlertException("A new attempt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        attempt = attemptService.save(attempt);
        return ResponseEntity.created(new URI("/api/attempts/" + attempt.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, attempt.getId().toString()))
            .body(attempt);
    }

    /**
     * {@code PUT  /attempts/:id} : Updates an existing attempt.
     *
     * @param id the id of the attempt to save.
     * @param attempt the attempt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attempt,
     * or with status {@code 400 (Bad Request)} if the attempt is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attempt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Attempt> updateAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Attempt attempt
    ) throws URISyntaxException {
        LOG.debug("REST request to update Attempt : {}, {}", id, attempt);
        if (attempt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attempt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        attempt = attemptService.update(attempt);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attempt.getId().toString()))
            .body(attempt);
    }

    /**
     * {@code PATCH  /attempts/:id} : Partial updates given fields of an existing attempt, field will ignore if it is null
     *
     * @param id the id of the attempt to save.
     * @param attempt the attempt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attempt,
     * or with status {@code 400 (Bad Request)} if the attempt is not valid,
     * or with status {@code 404 (Not Found)} if the attempt is not found,
     * or with status {@code 500 (Internal Server Error)} if the attempt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Attempt> partialUpdateAttempt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Attempt attempt
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update Attempt : {}, {}", id, attempt);
        if (attempt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attempt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attemptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Attempt> result = attemptService.partialUpdate(attempt);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, attempt.getId().toString())
        );
    }

    /**
     * {@code GET  /attempts} : get all the Attempts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Attempts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Attempt>> getAllAttempts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Attempts");
        Page<Attempt> page = attemptService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /attempts/:id} : get the "id" attempt.
     *
     * @param id the id of the attempt to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attempt, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Attempt> getAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Attempt : {}", id);
        Optional<Attempt> attempt = attemptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(attempt);
    }

    /**
     * {@code DELETE  /attempts/:id} : delete the "id" attempt.
     *
     * @param id the id of the attempt to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttempt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Attempt : {}", id);
        attemptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
