package com.examguard.repository;

import com.examguard.domain.ProctorEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ProctorEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProctorEventRepository extends JpaRepository<ProctorEvent, Long> {}
