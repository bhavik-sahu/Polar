package com.polar.logistics.repository;

import com.polar.logistics.entity.ReadinessRequirement;
import com.polar.logistics.entity.enums.PersonRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReadinessRequirementRepository extends JpaRepository<ReadinessRequirement, UUID> {
    List<ReadinessRequirement> findByIsMandatoryTrue();

    @Query("SELECT r FROM ReadinessRequirement r WHERE r.appliesToRole IS NULL OR r.appliesToRole = :role")
    List<ReadinessRequirement> findApplicableRequirements(@Param("role") PersonRole role);
}
