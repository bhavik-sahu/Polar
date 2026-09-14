package com.polar.logistics.repository;

import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.enums.PersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    List<Person> findByExpeditionId(UUID expeditionId);
    List<Person> findByCurrentStatus(PersonStatus status);

    @Query("SELECT p FROM Person p WHERE p.currentStatus IN ('IN_TRANSIT', 'AT_STATION') " +
           "AND (p.lastPingTime IS NULL OR p.lastPingTime < :cutoffTime)")
    List<Person> findPotentiallyInactivePersonnel(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT p.currentLocation as location, p.currentStatus as status, COUNT(p) as count " +
           "FROM Person p GROUP BY p.currentLocation, p.currentStatus")
    List<Object[]> getHeadcountSummary();
}
