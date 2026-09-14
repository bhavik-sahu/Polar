package com.polar.logistics.repository;

import com.polar.logistics.entity.EmergencyResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmergencyResponseLogRepository extends JpaRepository<EmergencyResponseLog, UUID> {
    List<EmergencyResponseLog> findByIncidentIdOrderByTimestampDesc(UUID incidentId);
}
