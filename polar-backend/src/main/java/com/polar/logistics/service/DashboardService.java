package com.polar.logistics.service;

import com.polar.logistics.dto.DashboardDtos;
import com.polar.logistics.dto.EmergencyDtos;
import com.polar.logistics.dto.ExpeditionDtos;
import com.polar.logistics.dto.InventoryDtos;
import com.polar.logistics.entity.CargoItem;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.entity.enums.ExpeditionStatus;
import com.polar.logistics.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ExpeditionRepository expeditionRepository;
    private final CargoItemRepository cargoItemRepository;
    private final InventoryManagementService inventoryManagementService;
    private final PersonnelMovementService personnelMovementService;
    private final EmergencyResponseService emergencyResponseService;

    public DashboardService(ExpeditionRepository expeditionRepository,
                            CargoItemRepository cargoItemRepository,
                            InventoryManagementService inventoryManagementService,
                            PersonnelMovementService personnelMovementService,
                            EmergencyResponseService emergencyResponseService) {
        this.expeditionRepository = expeditionRepository;
        this.cargoItemRepository = cargoItemRepository;
        this.inventoryManagementService = inventoryManagementService;
        this.personnelMovementService = personnelMovementService;
        this.emergencyResponseService = emergencyResponseService;
    }

    @Transactional(readOnly = true)
    public DashboardDtos.DashboardSummaryDto getDashboardSummary() {
        // 1. Expeditions
        List<Expedition> activeExpeditions = expeditionRepository.findByStatus(ExpeditionStatus.ACTIVE);
        if (activeExpeditions.isEmpty()) {
            activeExpeditions = expeditionRepository.findByStatusNot(ExpeditionStatus.COMPLETED);
        }

        List<ExpeditionDtos.ExpeditionSummaryDto> expeditionSummaries = activeExpeditions.stream().map(e ->
                ExpeditionDtos.ExpeditionSummaryDto.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .objective(e.getObjective())
                        .startDate(e.getStartDate())
                        .endDate(e.getEndDate())
                        .status(e.getStatus())
                        .totalLegs(e.getTransitLegs() != null ? e.getTransitLegs().size() : 0)
                        .build()
        ).collect(Collectors.toList());

        // 2. Cargo
        List<CargoItem> allCargo = cargoItemRepository.findAll();
        long inTransit = allCargo.stream().filter(c -> c.getStatus() == CargoStatus.IN_TRANSIT).count();
        long packed = allCargo.stream().filter(c -> c.getStatus() == CargoStatus.PACKED || c.getStatus() == CargoStatus.DISPATCHED).count();
        long arrivedOrStored = allCargo.stream().filter(c -> c.getStatus() == CargoStatus.ARRIVED || c.getStatus() == CargoStatus.STORED).count();
        double inTransitWeight = allCargo.stream()
                .filter(c -> c.getStatus() == CargoStatus.IN_TRANSIT && c.getWeightKg() != null)
                .mapToDouble(c -> c.getWeightKg().doubleValue())
                .sum();

        DashboardDtos.CargoSummary cargoSummary = DashboardDtos.CargoSummary.builder()
                .inTransitCount(inTransit)
                .packedOrDispatchedCount(packed)
                .arrivedOrStoredCount(arrivedOrStored)
                .totalCargoWeightInTransitKg(inTransitWeight)
                .build();

        // 3. Inventory Alerts
        List<InventoryDtos.InventoryAlertDto> inventoryAlerts = inventoryManagementService.getInventoryAlerts();

        // 4. Headcount
        var headcount = personnelMovementService.getHeadcountSummary();

        // 5. Emergencies
        List<EmergencyDtos.EmergencyIncidentDto> activeEmergencies = emergencyResponseService.getActiveEmergencies();

        return DashboardDtos.DashboardSummaryDto.builder()
                .activeExpeditionsCount(expeditionSummaries.size())
                .activeExpeditions(expeditionSummaries)
                .cargoSummary(cargoSummary)
                .inventoryAlerts(inventoryAlerts)
                .headcountBreakdown(headcount.getStationBreakdown())
                .activeEmergenciesCount(activeEmergencies.size())
                .activeEmergencies(activeEmergencies)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
