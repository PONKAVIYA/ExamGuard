package com.examguard.service;

import com.examguard.domain.Question;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.examguard.domain.Question}.
 */
public interface QuestionService {
    /**
     * Save a question.
     *
     * @param question the entity to save.
     * @return the persisted entity.
     */
    Question save(Question question);

    /**
     * Updates a question.
     *
     * @param question the entity to update.
     * @return the persisted entity.
     */
    Question update(Question question);

    /**
     * Partially updates a question.
     *
     * @param question the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Question> partialUpdate(Question question);

    /**
     * Get all the questions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Question> findAll(Pageable pageable);

    /**
     * Get the "id" question.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Question> findOne(Long id);

    /**
     * Delete the "id" question.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
