package com.isikato.infrastructure.repositories;

import com.isikato.infrastructure.entities.ContentVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ContentVisitRepository extends JpaRepository<ContentVisit, Long> {

    long countByContent_IdAndTimestampBetween(long id, LocalDateTime start, LocalDateTime end);
    long countByContent_Id(long id);
}
