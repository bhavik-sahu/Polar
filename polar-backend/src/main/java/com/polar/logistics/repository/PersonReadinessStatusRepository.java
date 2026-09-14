package com.polar.logistics.repository;

import com.polar.logistics.entity.PersonReadinessStatus;
import com.polar.logistics.entity.enums.ReadinessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonReadinessStatusRepository extends JpaRepository<PersonReadinessStatus, UUID> {
    List<PersonReadinessStatus> findByPersonId(UUID personId);

    @Query("SELECT prs FROM PersonReadinessStatus prs WHERE prs.person.expedition.id = :expeditionId")
    List<PersonReadinessStatus> findByExpeditionId(@Param("expeditionId") UUID expeditionId);

    Optional<PersonReadinessStatus> findByPersonIdAndRequirementId(UUID personId, UUID requirementId);

    @Query("SELECT COUNT(prs) FROM PersonReadinessStatus prs " +
           "WHERE prs.person.expedition.id = :expeditionId " +
           "AND prs.requirement.isMandatory = true " +
           "AND prs.status != 'VERIFIED'")
    long countUnresolvedMandatoryRequirementsForExpedition(@Param("expeditionId") UUID expeditionId);

    void deleteByPersonId(UUID personId);
}
