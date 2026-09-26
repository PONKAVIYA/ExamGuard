package com.examguard.service;

import com.examguard.domain.Exam;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.examguard.domain.Exam}.
 */
public interface ExamService {
    /**
     * Save a exam.
     *
     * @param exam the entity to save.
     * @return the persisted entity.
     */
    Exam save(Exam exam);

    /**
     * Updates a exam.
     *
     * @param exam the entity to update.
     * @return the persisted entity.
     */
    Exam update(Exam exam);

    /**
     * Partially updates a exam.
     *
     * @param exam the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Exam> partialUpdate(Exam exam);

    /**
     * Get all the exams.
     *
     * @return the list of entities.
     */
    List<Exam> findAll();

    /**
     * Get the "id" exam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Exam> findOne(Long id);

    /**
     * Delete the "id" exam.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
