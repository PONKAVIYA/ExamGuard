package com.examguard.repository;

import com.examguard.domain.Attempt;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Attempt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    @Query("select attempt from Attempt attempt where attempt.student.login = ?#{authentication.name}")
    List<Attempt> findByStudentIsCurrentUser();
}
