package com.examguard.web.rest;

import com.examguard.domain.ProctorEvent;
import com.examguard.repository.ProctorEventRepository;
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
 * REST controller for managing {@link com.examguard.domain.ProctorEvent}.
 */
@RestController
@RequestMapping("/api/proctor-events")
@Transactional(rollbackFor = Exception.class)
public class ProctorEventResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProctorEventResource.class);

    private static final String ENTITY_NAME = "proctorEvent";

    @Value("${jhipster.clientApp.name:examguard}")
    private String applicationName;

    private final ProctorEventRepository proctorEventRepository;

    public ProctorEventResource(ProctorEventRepository proctorEventRepository) {
        this.proctorEventRepository = proctorEventRepository;
    }

    /**
     * {@code POST  /proctor-events} : Create a new proctorEvent.
     *
     * @param proctorEvent the proctorEvent to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new proctorEvent, or with status {@code 400 (Bad Request)} if the proctorEvent already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProctorEvent> createProctorEvent(@Valid @RequestBody ProctorEvent proctorEvent) throws URISyntaxException {
        LOG.debug("REST request to save ProctorEvent : {}", proctorEvent);
        if (proctorEvent.getId() != null) {
            throw new BadRequestAlertException("A new proctorEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        proctorEvent = proctorEventRepository.save(proctorEvent);
        return ResponseEntity.created(new URI("/api/proctor-events/" + proctorEvent.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, proctorEvent.getId().toString()))
            .body(proctorEvent);
    }

    /**
     * {@code PUT  /proctor-events/:id} : Updates an existing proctorEvent.
     *
     * @param id the id of the proctorEvent to save.
     * @param proctorEvent the proctorEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proctorEvent,
     * or with status {@code 400 (Bad Request)} if the proctorEvent is not valid,
     * or with status {@code 500 (Internal Server Error)} if the proctorEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProctorEvent> updateProctorEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProctorEvent proctorEvent
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProctorEvent : {}, {}", id, proctorEvent);
        if (proctorEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proctorEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proctorEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        proctorEvent = proctorEventRepository.save(proctorEvent);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proctorEvent.getId().toString()))
            .body(proctorEvent);
    }

    /**
     * {@code PATCH  /proctor-events/:id} : Partial updates given fields of an existing proctorEvent, field will ignore if it is null
     *
     * @param id the id of the proctorEvent to save.
     * @param proctorEvent the proctorEvent to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated proctorEvent,
     * or with status {@code 400 (Bad Request)} if the proctorEvent is not valid,
     * or with status {@code 404 (Not Found)} if the proctorEvent is not found,
     * or with status {@code 500 (Internal Server Error)} if the proctorEvent couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProctorEvent> partialUpdateProctorEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProctorEvent proctorEvent
    ) throws URISyntaxException {
        LOG.debug("REST request to partially update ProctorEvent : {}, {}", id, proctorEvent);
        if (proctorEvent.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, proctorEvent.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!proctorEventRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProctorEvent> result = proctorEventRepository
            .findById(proctorEvent.getId())
            .map(existingProctorEvent -> {
                updateIfPresent(existingProctorEvent::setEventType, proctorEvent.getEventType());
                updateIfPresent(existingProctorEvent::setTimestamp, proctorEvent.getTimestamp());

                return existingProctorEvent;
            })
            .map(proctorEventRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, proctorEvent.getId().toString())
        );
    }

    /**
     * {@code GET  /proctor-events} : get all the Proctor Events.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Proctor Events in body.
     */
    @GetMapping("")
    public List<ProctorEvent> getAllProctorEvents() {
        LOG.debug("REST request to get all ProctorEvents");
        return proctorEventRepository.findAll();
    }

    /**
     * {@code GET  /proctor-events/:id} : get the "id" proctorEvent.
     *
     * @param id the id of the proctorEvent to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the proctorEvent, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProctorEvent> getProctorEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProctorEvent : {}", id);
        Optional<ProctorEvent> proctorEvent = proctorEventRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(proctorEvent);
    }

    /**
     * {@code DELETE  /proctor-events/:id} : delete the "id" proctorEvent.
     *
     * @param id the id of the proctorEvent to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProctorEvent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProctorEvent : {}", id);
        proctorEventRepository.deleteById(id);
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
