package com.examguard.repository;

import com.examguard.domain.StudentAnswer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StudentAnswer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {}
