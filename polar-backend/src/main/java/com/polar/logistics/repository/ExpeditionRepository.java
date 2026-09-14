package com.polar.logistics.repository;

import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.enums.ExpeditionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpeditionRepository extends JpaRepository<Expedition, UUID> {
    List<Expedition> findByStatus(ExpeditionStatus status);
    List<Expedition> findByStatusNot(ExpeditionStatus status);

    @Query("SELECT e FROM Expedition e WHERE e.status != 'COMPLETED' AND e.status != 'CANCELLED' " +
           "AND NOT (e.endDate < :startDate OR e.startDate > :endDate)")
    List<Expedition> findOverlappingExpeditions(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
