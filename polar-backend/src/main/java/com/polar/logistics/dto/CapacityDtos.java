package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.TransitMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class CapacityDtos {

    private CapacityDtos() {}

    public static class LegCapacityDto {
        private UUID legId;
        private Integer sequenceOrder;
        private String origin;
        private String destination;
        private TransitMode mode;
        private BigDecimal allocatedWeightKg;
        private BigDecimal maxWeightCapacityKg;
        private BigDecimal remainingCapacityKg;
        private Double utilizationPercent;
        private String statusBand; // GREEN, AMBER, RED
        private Integer allocatedCargoCount;
        private List<CargoDtos.CargoItemDto> allocatedCargo;

        public LegCapacityDto() {}

        public LegCapacityDto(UUID legId, Integer sequenceOrder, String origin, String destination, TransitMode mode,
                              BigDecimal allocatedWeightKg, BigDecimal maxWeightCapacityKg, BigDecimal remainingCapacityKg,
                              Double utilizationPercent, String statusBand, Integer allocatedCargoCount,
                              List<CargoDtos.CargoItemDto> allocatedCargo) {
            this.legId = legId;
            this.sequenceOrder = sequenceOrder;
            this.origin = origin;
            this.destination = destination;
            this.mode = mode;
            this.allocatedWeightKg = allocatedWeightKg;
            this.maxWeightCapacityKg = maxWeightCapacityKg;
            this.remainingCapacityKg = remainingCapacityKg;
            this.utilizationPercent = utilizationPercent;
            this.statusBand = statusBand;
            this.allocatedCargoCount = allocatedCargoCount;
            this.allocatedCargo = allocatedCargo;
        }

        public UUID getLegId() { return legId; }
        public void setLegId(UUID legId) { this.legId = legId; }

        public Integer getSequenceOrder() { return sequenceOrder; }
        public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }

        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }

        public TransitMode getMode() { return mode; }
        public void setMode(TransitMode mode) { this.mode = mode; }

        public BigDecimal getAllocatedWeightKg() { return allocatedWeightKg; }
        public void setAllocatedWeightKg(BigDecimal allocatedWeightKg) { this.allocatedWeightKg = allocatedWeightKg; }

        public BigDecimal getMaxWeightCapacityKg() { return maxWeightCapacityKg; }
        public void setMaxWeightCapacityKg(BigDecimal maxWeightCapacityKg) { this.maxWeightCapacityKg = maxWeightCapacityKg; }

        public BigDecimal getRemainingCapacityKg() { return remainingCapacityKg; }
        public void setRemainingCapacityKg(BigDecimal remainingCapacityKg) { this.remainingCapacityKg = remainingCapacityKg; }

        public Double getUtilizationPercent() { return utilizationPercent; }
        public void setUtilizationPercent(Double utilizationPercent) { this.utilizationPercent = utilizationPercent; }

        public String getStatusBand() { return statusBand; }
        public void setStatusBand(String statusBand) { this.statusBand = statusBand; }

        public Integer getAllocatedCargoCount() { return allocatedCargoCount; }
        public void setAllocatedCargoCount(Integer allocatedCargoCount) { this.allocatedCargoCount = allocatedCargoCount; }

        public List<CargoDtos.CargoItemDto> getAllocatedCargo() { return allocatedCargo; }
        public void setAllocatedCargo(List<CargoDtos.CargoItemDto> allocatedCargo) { this.allocatedCargo = allocatedCargo; }
    }

    public static class ExpeditionCapacityOverviewDto {
        private UUID expeditionId;
        private String expeditionName;
        private BigDecimal totalAllocatedWeightKg;
        private BigDecimal totalMaxCapacityKg;
        private Double overallUtilizationPercent;
        private String overallStatusBand;
        private List<LegCapacityDto> legs;

        public ExpeditionCapacityOverviewDto() {}

        public ExpeditionCapacityOverviewDto(UUID expeditionId, String expeditionName, BigDecimal totalAllocatedWeightKg,
                                             BigDecimal totalMaxCapacityKg, Double overallUtilizationPercent,
                                             String overallStatusBand, List<LegCapacityDto> legs) {
            this.expeditionId = expeditionId;
            this.expeditionName = expeditionName;
            this.totalAllocatedWeightKg = totalAllocatedWeightKg;
            this.totalMaxCapacityKg = totalMaxCapacityKg;
            this.overallUtilizationPercent = overallUtilizationPercent;
            this.overallStatusBand = overallStatusBand;
            this.legs = legs;
        }

        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }

        public String getExpeditionName() { return expeditionName; }
        public void setExpeditionName(String expeditionName) { this.expeditionName = expeditionName; }

        public BigDecimal getTotalAllocatedWeightKg() { return totalAllocatedWeightKg; }
        public void setTotalAllocatedWeightKg(BigDecimal totalAllocatedWeightKg) { this.totalAllocatedWeightKg = totalAllocatedWeightKg; }

        public BigDecimal getTotalMaxCapacityKg() { return totalMaxCapacityKg; }
        public void setTotalMaxCapacityKg(BigDecimal totalMaxCapacityKg) { this.totalMaxCapacityKg = totalMaxCapacityKg; }

        public Double getOverallUtilizationPercent() { return overallUtilizationPercent; }
        public void setOverallUtilizationPercent(Double overallUtilizationPercent) { this.overallUtilizationPercent = overallUtilizationPercent; }

        public String getOverallStatusBand() { return overallStatusBand; }
        public void setOverallStatusBand(String overallStatusBand) { this.overallStatusBand = overallStatusBand; }

        public List<LegCapacityDto> getLegs() { return legs; }
        public void setLegs(List<LegCapacityDto> legs) { this.legs = legs; }
    }

    public static class UpdateCapacityRequest {
        @NotNull(message = "Max weight capacity is required")
        @DecimalMin(value = "0.01", message = "Max weight capacity must be greater than 0")
        private BigDecimal maxWeightCapacityKg;

        private BigDecimal maxVolumeCapacityM3;

        public UpdateCapacityRequest() {}

        public UpdateCapacityRequest(BigDecimal maxWeightCapacityKg, BigDecimal maxVolumeCapacityM3) {
            this.maxWeightCapacityKg = maxWeightCapacityKg;
            this.maxVolumeCapacityM3 = maxVolumeCapacityM3;
        }

        public BigDecimal getMaxWeightCapacityKg() { return maxWeightCapacityKg; }
        public void setMaxWeightCapacityKg(BigDecimal maxWeightCapacityKg) { this.maxWeightCapacityKg = maxWeightCapacityKg; }

        public BigDecimal getMaxVolumeCapacityM3() { return maxVolumeCapacityM3; }
        public void setMaxVolumeCapacityM3(BigDecimal maxVolumeCapacityM3) { this.maxVolumeCapacityM3 = maxVolumeCapacityM3; }
    }

    public static class CapacityCheckRequest {
        @NotNull(message = "Additional weight is required")
        @DecimalMin(value = "0.00", message = "Additional weight cannot be negative")
        private BigDecimal additionalWeightKg;

        public CapacityCheckRequest() {}

        public CapacityCheckRequest(BigDecimal additionalWeightKg) {
            this.additionalWeightKg = additionalWeightKg;
        }

        public BigDecimal getAdditionalWeightKg() { return additionalWeightKg; }
        public void setAdditionalWeightKg(BigDecimal additionalWeightKg) { this.additionalWeightKg = additionalWeightKg; }
    }

    public static class CapacityCheckResponse {
        private boolean willExceed;
        private BigDecimal currentAllocatedWeightKg;
        private BigDecimal projectedAllocatedWeightKg;
        private BigDecimal maxWeightCapacityKg;
        private Double projectedUtilizationPercent;
        private String statusBand;
        private String warningMessage;

        public CapacityCheckResponse() {}

        public CapacityCheckResponse(boolean willExceed, BigDecimal currentAllocatedWeightKg,
                                     BigDecimal projectedAllocatedWeightKg, BigDecimal maxWeightCapacityKg,
                                     Double projectedUtilizationPercent, String statusBand, String warningMessage) {
            this.willExceed = willExceed;
            this.currentAllocatedWeightKg = currentAllocatedWeightKg;
            this.projectedAllocatedWeightKg = projectedAllocatedWeightKg;
            this.maxWeightCapacityKg = maxWeightCapacityKg;
            this.projectedUtilizationPercent = projectedUtilizationPercent;
            this.statusBand = statusBand;
            this.warningMessage = warningMessage;
        }

        public boolean isWillExceed() { return willExceed; }
        public void setWillExceed(boolean willExceed) { this.willExceed = willExceed; }

        public BigDecimal getCurrentAllocatedWeightKg() { return currentAllocatedWeightKg; }
        public void setCurrentAllocatedWeightKg(BigDecimal currentAllocatedWeightKg) { this.currentAllocatedWeightKg = currentAllocatedWeightKg; }

        public BigDecimal getProjectedAllocatedWeightKg() { return projectedAllocatedWeightKg; }
        public void setProjectedAllocatedWeightKg(BigDecimal projectedAllocatedWeightKg) { this.projectedAllocatedWeightKg = projectedAllocatedWeightKg; }

        public BigDecimal getMaxWeightCapacityKg() { return maxWeightCapacityKg; }
        public void setMaxWeightCapacityKg(BigDecimal maxWeightCapacityKg) { this.maxWeightCapacityKg = maxWeightCapacityKg; }

        public Double getProjectedUtilizationPercent() { return projectedUtilizationPercent; }
        public void setProjectedUtilizationPercent(Double projectedUtilizationPercent) { this.projectedUtilizationPercent = projectedUtilizationPercent; }

        public String getStatusBand() { return statusBand; }
        public void setStatusBand(String statusBand) { this.statusBand = statusBand; }

        public String getWarningMessage() { return warningMessage; }
        public void setWarningMessage(String warningMessage) { this.warningMessage = warningMessage; }
    }
}
