package com.examguard.repository;

import com.examguard.domain.QuestionOption;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuestionOption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {}
