package com.polar.logistics.repository;

import com.polar.logistics.entity.InventoryItem;
import com.polar.logistics.entity.enums.StationName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    List<InventoryItem> findByStation(StationName station);
    Optional<InventoryItem> findByNameIgnoreCaseAndStation(String name, StationName station);
    Optional<InventoryItem> findByNameIgnoreCaseAndCategoryIgnoreCaseAndStation(String name, String category, StationName station);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.reorderThreshold")
    List<InventoryItem> findLowStockItems();

    @Query("SELECT i FROM InventoryItem i WHERE i.expiryDate IS NOT NULL AND i.expiryDate <= :expiryThreshold")
    List<InventoryItem> findExpiringItems(@Param("expiryThreshold") LocalDate expiryThreshold);

    @Query("SELECT i FROM InventoryItem i ORDER BY (CAST(i.quantity AS double) / CASE WHEN i.reorderThreshold = 0 THEN 1 ELSE i.reorderThreshold END) ASC")
    List<InventoryItem> findAllSortedByUrgency();

    @Query("SELECT i FROM InventoryItem i WHERE i.station = :station ORDER BY (CAST(i.quantity AS double) / CASE WHEN i.reorderThreshold = 0 THEN 1 ELSE i.reorderThreshold END) ASC")
    List<InventoryItem> findByStationSortedByUrgency(@Param("station") StationName station);
}
