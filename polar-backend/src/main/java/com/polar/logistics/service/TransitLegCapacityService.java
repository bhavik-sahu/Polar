package com.polar.logistics.service;

import com.polar.logistics.dto.CapacityDtos;
import com.polar.logistics.dto.CargoDtos;
import com.polar.logistics.entity.CargoItem;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.TransitLeg;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.repository.CargoItemRepository;
import com.polar.logistics.repository.ExpeditionRepository;
import com.polar.logistics.repository.TransitLegRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransitLegCapacityService {

    public static final BigDecimal DEFAULT_LEG_CAPACITY_KG = new BigDecimal("20000.00"); // 20 MT default

    private final TransitLegRepository transitLegRepository;
    private final CargoItemRepository cargoItemRepository;
    private final ExpeditionRepository expeditionRepository;

    public TransitLegCapacityService(TransitLegRepository transitLegRepository,
                                    CargoItemRepository cargoItemRepository,
                                    ExpeditionRepository expeditionRepository) {
        this.transitLegRepository = transitLegRepository;
        this.cargoItemRepository = cargoItemRepository;
        this.expeditionRepository = expeditionRepository;
    }

    @Transactional(readOnly = true)
    public CapacityDtos.LegCapacityDto getCapacityStatus(UUID legId) {
        TransitLeg leg = transitLegRepository.findById(legId)
                .orElseThrow(() -> new ResourceNotFoundException("Transit leg not found with ID: " + legId));

        BigDecimal maxCapacity = leg.getMaxWeightCapacityKg() != null ? leg.getMaxWeightCapacityKg() : DEFAULT_LEG_CAPACITY_KG;

        // Fetch all cargo items assigned to this leg (or belonging to expedition and on this leg) where status != CONSUMED
        List<CargoItem> allCargo = cargoItemRepository.findAll();
        List<CargoItem> legCargo = allCargo.stream()
                .filter(c -> c.getStatus() != CargoStatus.CONSUMED)
                .filter(c -> (c.getCurrentTransitLeg() != null && c.getCurrentTransitLeg().getId().equals(legId)) ||
                             (c.getCurrentTransitLeg() == null && c.getExpedition() != null && leg.getExpedition() != null && c.getExpedition().getId().equals(leg.getExpedition().getId())))
                .sorted(Comparator.comparing(CargoItem::getWeightKg, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        BigDecimal allocatedWeight = legCargo.stream()
                .filter(c -> c.getWeightKg() != null)
                .map(CargoItem::getWeightKg)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double utilizationPercent = maxCapacity.compareTo(BigDecimal.ZERO) > 0
                ? allocatedWeight.divide(maxCapacity, 4, RoundingMode.HALF_UP).doubleValue() * 100.0
                : 0.0;

        BigDecimal remainingCapacity = maxCapacity.subtract(allocatedWeight);

        String statusBand;
        if (utilizationPercent < 80.0) {
            statusBand = "GREEN";
        } else if (utilizationPercent <= 100.0) {
            statusBand = "AMBER";
        } else {
            statusBand = "RED";
        }

        List<CargoDtos.CargoItemDto> cargoDtos = legCargo.stream()
                .map(c -> CargoDtos.CargoItemDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .category(c.getCategory())
                        .weightKg(c.getWeightKg())
                        .status(c.getStatus())
                        .expeditionId(c.getExpedition() != null ? c.getExpedition().getId() : null)
                        .expeditionName(c.getExpedition() != null ? c.getExpedition().getName() : null)
                        .currentStationLocation(c.getCurrentStationLocation())
                        .lastKnownLatitude(c.getLastKnownLatitude())
                        .lastKnownLongitude(c.getLastKnownLongitude())
                        .lastPingTime(c.getLastPingTime())
                        .build())
                .collect(Collectors.toList());

        return new CapacityDtos.LegCapacityDto(
                leg.getId(),
                leg.getSequenceOrder(),
                leg.getOrigin(),
                leg.getDestination(),
                leg.getMode(),
                allocatedWeight,
                maxCapacity,
                remainingCapacity,
                Math.round(utilizationPercent * 10.0) / 10.0,
                statusBand,
                legCargo.size(),
                cargoDtos
        );
    }

    @Transactional(readOnly = true)
    public CapacityDtos.ExpeditionCapacityOverviewDto getExpeditionCapacityOverview(UUID expeditionId) {
        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        List<TransitLeg> legs = transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(expeditionId);
        List<CapacityDtos.LegCapacityDto> legDtos = new ArrayList<>();

        BigDecimal totalAllocated = BigDecimal.ZERO;
        BigDecimal totalMax = BigDecimal.ZERO;

        for (TransitLeg leg : legs) {
            CapacityDtos.LegCapacityDto dto = getCapacityStatus(leg.getId());
            legDtos.add(dto);
            totalAllocated = totalAllocated.add(dto.getAllocatedWeightKg());
            totalMax = totalMax.add(dto.getMaxWeightCapacityKg());
        }

        double overallUtilization = totalMax.compareTo(BigDecimal.ZERO) > 0
                ? totalAllocated.divide(totalMax, 4, RoundingMode.HALF_UP).doubleValue() * 100.0
                : 0.0;

        String overallBand = overallUtilization < 80.0 ? "GREEN" : (overallUtilization <= 100.0 ? "AMBER" : "RED");

        return new CapacityDtos.ExpeditionCapacityOverviewDto(
                expedition.getId(),
                expedition.getName(),
                totalAllocated,
                totalMax,
                Math.round(overallUtilization * 10.0) / 10.0,
                overallBand,
                legDtos
        );
    }

    @Transactional
    public CapacityDtos.LegCapacityDto updateLegCapacity(UUID legId, CapacityDtos.UpdateCapacityRequest request) {
        TransitLeg leg = transitLegRepository.findById(legId)
                .orElseThrow(() -> new ResourceNotFoundException("Transit leg not found with ID: " + legId));

        leg.setMaxWeightCapacityKg(request.getMaxWeightCapacityKg());
        if (request.getMaxVolumeCapacityM3() != null) {
            leg.setMaxVolumeCapacityM3(request.getMaxVolumeCapacityM3());
        }
        transitLegRepository.save(leg);

        return getCapacityStatus(legId);
    }

    @Transactional(readOnly = true)
    public CapacityDtos.CapacityCheckResponse checkCapacityPreCheck(UUID legId, BigDecimal additionalWeightKg) {
        CapacityDtos.LegCapacityDto current = getCapacityStatus(legId);

        BigDecimal addWeight = additionalWeightKg != null ? additionalWeightKg : BigDecimal.ZERO;
        BigDecimal projectedWeight = current.getAllocatedWeightKg().add(addWeight);
        BigDecimal maxCap = current.getMaxWeightCapacityKg();

        double projectedUtil = maxCap.compareTo(BigDecimal.ZERO) > 0
                ? projectedWeight.divide(maxCap, 4, RoundingMode.HALF_UP).doubleValue() * 100.0
                : 0.0;

        boolean willExceed = projectedUtil > 100.0;
        String band = projectedUtil < 80.0 ? "GREEN" : (projectedUtil <= 100.0 ? "AMBER" : "RED");

        String warning = null;
        if (willExceed) {
            warning = String.format("Capacity Over-Commitment Warning: Adding %s kg will put transit leg [%s → %s] at %.1f%% capacity (%s kg / %s kg).",
                    addWeight.toPlainString(), current.getOrigin(), current.getDestination(), projectedUtil,
                    projectedWeight.toPlainString(), maxCap.toPlainString());
        }

        return new CapacityDtos.CapacityCheckResponse(
                willExceed,
                current.getAllocatedWeightKg(),
                projectedWeight,
                maxCap,
                Math.round(projectedUtil * 10.0) / 10.0,
                band,
                warning
        );
    }
}
