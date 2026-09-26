package com.examguard.service;

import com.examguard.domain.Attempt;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.examguard.domain.Attempt}.
 */
public interface AttemptService {
    /**
     * Save a attempt.
     *
     * @param attempt the entity to save.
     * @return the persisted entity.
     */
    Attempt save(Attempt attempt);

    /**
     * Updates a attempt.
     *
     * @param attempt the entity to update.
     * @return the persisted entity.
     */
    Attempt update(Attempt attempt);

    /**
     * Partially updates a attempt.
     *
     * @param attempt the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Attempt> partialUpdate(Attempt attempt);

    /**
     * Get all the attempts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Attempt> findAll(Pageable pageable);

    /**
     * Get the "id" attempt.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Attempt> findOne(Long id);

    /**
     * Delete the "id" attempt.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
