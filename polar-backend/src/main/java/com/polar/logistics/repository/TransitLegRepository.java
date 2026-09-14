package com.polar.logistics.repository;

import com.polar.logistics.entity.TransitLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransitLegRepository extends JpaRepository<TransitLeg, UUID> {
    List<TransitLeg> findByExpeditionIdOrderBySequenceOrderAsc(UUID expeditionId);
    void deleteByExpeditionId(UUID expeditionId);
}
