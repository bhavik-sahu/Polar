package com.polar.logistics.repository;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.entity.enums.PingEntityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatusPingRepository extends MongoRepository<StatusPing, String> {
    List<StatusPing> findByEntityTypeAndEntityIdOrderByTimestampDesc(PingEntityType entityType, UUID entityId);
    Optional<StatusPing> findFirstByEntityTypeAndEntityIdOrderByTimestampDesc(PingEntityType entityType, UUID entityId);
    List<StatusPing> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime from, LocalDateTime to);
}
