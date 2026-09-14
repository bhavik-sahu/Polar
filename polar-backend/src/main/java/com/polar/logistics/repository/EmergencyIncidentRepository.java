package com.polar.logistics.repository;

import com.polar.logistics.entity.EmergencyIncident;
import com.polar.logistics.entity.enums.EmergencyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmergencyIncidentRepository extends JpaRepository<EmergencyIncident, UUID> {
    List<EmergencyIncident> findByStatusOrderByTriggeredAtDesc(EmergencyStatus status);
    List<EmergencyIncident> findAllByOrderByTriggeredAtDesc();

    @Query("SELECT COUNT(e) > 0 FROM EmergencyIncident e WHERE e.triggeredBy.id = :personId AND e.status = 'ACTIVE'")
    boolean existsActiveIncidentForPerson(@Param("personId") UUID personId);
}
