package com.polar.logistics.service;

import com.polar.logistics.dto.CargoDtos;
import com.polar.logistics.entity.*;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.entity.enums.StationLocation;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.exception.InvalidStateTransitionException;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CargoTrackingService {

    private static final Logger log = LoggerFactory.getLogger(CargoTrackingService.class);

    private final CargoItemRepository cargoItemRepository;
    private final ExpeditionRepository expeditionRepository;
    private final TransitLegRepository transitLegRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public CargoTrackingService(CargoItemRepository cargoItemRepository,
                                ExpeditionRepository expeditionRepository,
                                TransitLegRepository transitLegRepository,
                                InventoryItemRepository inventoryItemRepository) {
        this.cargoItemRepository = cargoItemRepository;
        this.expeditionRepository = expeditionRepository;
        this.transitLegRepository = transitLegRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional
    public CargoDtos.CargoItemDto createCargo(CargoDtos.CreateCargoRequest request) {
        String duplicateWarning = null;

        // Duplicate-stock check before creation at destination station
        if (request.getDestinationStation() != null &&
                (request.getDestinationStation() == StationLocation.MAITRI || request.getDestinationStation() == StationLocation.BHARATI)) {
            StationName station = StationName.valueOf(request.getDestinationStation().name());
            Optional<InventoryItem> existingStock = inventoryItemRepository
                    .findByNameIgnoreCaseAndCategoryIgnoreCaseAndStation(request.getName(), request.getCategory().name(), station);

            if (existingStock.isPresent() && existingStock.get().getQuantity() > 0) {
                duplicateWarning = String.format("Duplicate Stock Notice: %s currently has %d %s of '%s' in inventory.",
                        station, existingStock.get().getQuantity(), existingStock.get().getUnit(), request.getName());
                log.info(duplicateWarning);
            }
        }

        Expedition expedition = null;
        if (request.getExpeditionId() != null) {
            expedition = expeditionRepository.findById(request.getExpeditionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + request.getExpeditionId()));
        }

        TransitLeg transitLeg = null;
        if (request.getCurrentTransitLegId() != null) {
            transitLeg = transitLegRepository.findById(request.getCurrentTransitLegId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transit leg not found with ID: " + request.getCurrentTransitLegId()));
        }

        CargoItem cargo = CargoItem.builder()
                .name(request.getName())
                .category(request.getCategory())
                .weightKg(request.getWeightKg())
                .status(CargoStatus.PACKED)
                .expedition(expedition)
                .currentTransitLeg(transitLeg)
                .currentStationLocation(request.getDestinationStation() != null ? request.getDestinationStation() : StationLocation.INDIA)
                .build();

        CargoItem saved = cargoItemRepository.save(cargo);
        CargoDtos.CargoItemDto dto = mapToDto(saved);
        dto.setWarning(duplicateWarning);
        return dto;
    }

    @Transactional
    public CargoDtos.CargoItemDto updateStatus(UUID cargoId, CargoDtos.UpdateCargoStatusRequest request) {
        CargoItem cargo = cargoItemRepository.findById(cargoId)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo item not found with ID: " + cargoId));

        if (!cargo.getStatus().canTransitionTo(request.getStatus())) {
            throw new InvalidStateTransitionException(String.format(
                    "Illegal status transition for cargo '%s': Cannot move from %s to %s",
                    cargo.getName(), cargo.getStatus(), request.getStatus()
            ));
        }

        cargo.setStatus(request.getStatus());

        if (request.getStationLocation() != null) {
            cargo.setCurrentStationLocation(request.getStationLocation());
        }

        if (request.getCurrentTransitLegId() != null) {
            TransitLeg leg = transitLegRepository.findById(request.getCurrentTransitLegId()).orElse(null);
            cargo.setCurrentTransitLeg(leg);
        } else if (request.getStatus() == CargoStatus.ARRIVED || request.getStatus() == CargoStatus.STORED) {
            cargo.setCurrentTransitLeg(null);
        }

        // Auto-link to Inventory on STORED transition
        if (request.getStatus() == CargoStatus.STORED) {
            StationName targetStation = (cargo.getCurrentStationLocation() == StationLocation.BHARATI)
                    ? StationName.BHARATI
                    : StationName.MAITRI;

            InventoryItem inventoryItem = inventoryItemRepository
                    .findByNameIgnoreCaseAndStation(cargo.getName(), targetStation)
                    .orElseGet(() -> {
                        InventoryItem newItem = InventoryItem.builder()
                                .name(cargo.getName())
                                .category(cargo.getCategory().name())
                                .station(targetStation)
                                .quantity(0)
                                .unit("kg")
                                .reorderThreshold(10)
                                .lastUpdated(LocalDateTime.now())
                                .build();
                        return inventoryItemRepository.save(newItem);
                    });

            int addQuantity = Math.max(1, cargo.getWeightKg().intValue());
            inventoryItem.setQuantity(inventoryItem.getQuantity() + addQuantity);
            inventoryItem.setLastUpdated(LocalDateTime.now());
            inventoryItemRepository.save(inventoryItem);

            cargo.setLinkedInventoryItem(inventoryItem);
            log.info("Cargo item '{}' stored. Updated InventoryItem ID: {}, New Quantity: {}",
                    cargo.getName(), inventoryItem.getId(), inventoryItem.getQuantity());
        }

        return mapToDto(cargoItemRepository.save(cargo));
    }

    @Transactional
    public CargoDtos.CargoItemDto assignToExpedition(UUID cargoId, UUID expeditionId) {
        CargoItem cargo = cargoItemRepository.findById(cargoId)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo item not found with ID: " + cargoId));

        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        cargo.setExpedition(expedition);
        return mapToDto(cargoItemRepository.save(cargo));
    }

    @Transactional(readOnly = true)
    public CargoDtos.CargoItemDto getCargoById(UUID id) {
        CargoItem cargo = cargoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo item not found with ID: " + id));
        return mapToDto(cargo);
    }

    @Transactional(readOnly = true)
    public List<CargoDtos.CargoItemDto> getAllCargo(UUID expeditionId, CargoStatus status) {
        List<CargoItem> list;
        if (expeditionId != null) {
            list = cargoItemRepository.findByExpeditionId(expeditionId);
        } else if (status != null) {
            list = cargoItemRepository.findByStatus(status);
        } else {
            list = cargoItemRepository.findAll();
        }
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCargo(UUID id) {
        if (!cargoItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cargo item not found with ID: " + id);
        }
        cargoItemRepository.deleteById(id);
    }

    private CargoDtos.CargoItemDto mapToDto(CargoItem c) {
        return CargoDtos.CargoItemDto.builder()
                .id(c.getId())
                .name(c.getName())
                .category(c.getCategory())
                .weightKg(c.getWeightKg())
                .status(c.getStatus())
                .expeditionId(c.getExpedition() != null ? c.getExpedition().getId() : null)
                .expeditionName(c.getExpedition() != null ? c.getExpedition().getName() : null)
                .currentTransitLegId(c.getCurrentTransitLeg() != null ? c.getCurrentTransitLeg().getId() : null)
                .currentTransitLegInfo(c.getCurrentTransitLeg() != null ?
                        c.getCurrentTransitLeg().getOrigin() + " → " + c.getCurrentTransitLeg().getDestination() : null)
                .currentStationLocation(c.getCurrentStationLocation())
                .linkedInventoryItemId(c.getLinkedInventoryItem() != null ? c.getLinkedInventoryItem().getId() : null)
                .lastKnownLatitude(c.getLastKnownLatitude())
                .lastKnownLongitude(c.getLastKnownLongitude())
                .lastPingTime(c.getLastPingTime())
                .build();
    }
}
