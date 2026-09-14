package com.polar.logistics.repository;

import com.polar.logistics.entity.CargoItem;
import com.polar.logistics.entity.enums.CargoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CargoItemRepository extends JpaRepository<CargoItem, UUID> {
    List<CargoItem> findByExpeditionId(UUID expeditionId);
    List<CargoItem> findByStatus(CargoStatus status);
    List<CargoItem> findByStatusIn(List<CargoStatus> statuses);
}
