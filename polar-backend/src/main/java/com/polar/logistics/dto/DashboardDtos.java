package com.polar.logistics.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DashboardDtos {

    public static class DashboardSummaryDto {
        private long activeExpeditionsCount;
        private List<ExpeditionDtos.ExpeditionSummaryDto> activeExpeditions;
        private CargoSummary cargoSummary;
        private List<InventoryDtos.InventoryAlertDto> inventoryAlerts;
        private Map<String, Map<String, Long>> headcountBreakdown;
        private long activeEmergenciesCount;
        private List<EmergencyDtos.EmergencyIncidentDto> activeEmergencies;
        private LocalDateTime generatedAt = LocalDateTime.now();

        public DashboardSummaryDto() {}
        public DashboardSummaryDto(long activeExpeditionsCount, List<ExpeditionDtos.ExpeditionSummaryDto> activeExpeditions,
                                   CargoSummary cargoSummary, List<InventoryDtos.InventoryAlertDto> inventoryAlerts,
                                   Map<String, Map<String, Long>> headcountBreakdown, long activeEmergenciesCount,
                                   List<EmergencyDtos.EmergencyIncidentDto> activeEmergencies, LocalDateTime generatedAt) {
            this.activeExpeditionsCount = activeExpeditionsCount;
            this.activeExpeditions = activeExpeditions;
            this.cargoSummary = cargoSummary;
            this.inventoryAlerts = inventoryAlerts;
            this.headcountBreakdown = headcountBreakdown;
            this.activeEmergenciesCount = activeEmergenciesCount;
            this.activeEmergencies = activeEmergencies;
            this.generatedAt = generatedAt != null ? generatedAt : LocalDateTime.now();
        }

        public long getActiveExpeditionsCount() { return activeExpeditionsCount; }
        public void setActiveExpeditionsCount(long activeExpeditionsCount) { this.activeExpeditionsCount = activeExpeditionsCount; }
        public List<ExpeditionDtos.ExpeditionSummaryDto> getActiveExpeditions() { return activeExpeditions; }
        public void setActiveExpeditions(List<ExpeditionDtos.ExpeditionSummaryDto> activeExpeditions) { this.activeExpeditions = activeExpeditions; }
        public CargoSummary getCargoSummary() { return cargoSummary; }
        public void setCargoSummary(CargoSummary cargoSummary) { this.cargoSummary = cargoSummary; }
        public List<InventoryDtos.InventoryAlertDto> getInventoryAlerts() { return inventoryAlerts; }
        public void setInventoryAlerts(List<InventoryDtos.InventoryAlertDto> inventoryAlerts) { this.inventoryAlerts = inventoryAlerts; }
        public Map<String, Map<String, Long>> getHeadcountBreakdown() { return headcountBreakdown; }
        public void setHeadcountBreakdown(Map<String, Map<String, Long>> headcountBreakdown) { this.headcountBreakdown = headcountBreakdown; }
        public long getActiveEmergenciesCount() { return activeEmergenciesCount; }
        public void setActiveEmergenciesCount(long activeEmergenciesCount) { this.activeEmergenciesCount = activeEmergenciesCount; }
        public List<EmergencyDtos.EmergencyIncidentDto> getActiveEmergencies() { return activeEmergencies; }
        public void setActiveEmergencies(List<EmergencyDtos.EmergencyIncidentDto> activeEmergencies) { this.activeEmergencies = activeEmergencies; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private long activeExpeditionsCount;
            private List<ExpeditionDtos.ExpeditionSummaryDto> activeExpeditions;
            private CargoSummary cargoSummary;
            private List<InventoryDtos.InventoryAlertDto> inventoryAlerts;
            private Map<String, Map<String, Long>> headcountBreakdown;
            private long activeEmergenciesCount;
            private List<EmergencyDtos.EmergencyIncidentDto> activeEmergencies;
            private LocalDateTime generatedAt = LocalDateTime.now();

            public Builder activeExpeditionsCount(long activeExpeditionsCount) { this.activeExpeditionsCount = activeExpeditionsCount; return this; }
            public Builder activeExpeditions(List<ExpeditionDtos.ExpeditionSummaryDto> activeExpeditions) { this.activeExpeditions = activeExpeditions; return this; }
            public Builder cargoSummary(CargoSummary cargoSummary) { this.cargoSummary = cargoSummary; return this; }
            public Builder inventoryAlerts(List<InventoryDtos.InventoryAlertDto> inventoryAlerts) { this.inventoryAlerts = inventoryAlerts; return this; }
            public Builder headcountBreakdown(Map<String, Map<String, Long>> headcountBreakdown) { this.headcountBreakdown = headcountBreakdown; return this; }
            public Builder activeEmergenciesCount(long activeEmergenciesCount) { this.activeEmergenciesCount = activeEmergenciesCount; return this; }
            public Builder activeEmergencies(List<EmergencyDtos.EmergencyIncidentDto> activeEmergencies) { this.activeEmergencies = activeEmergencies; return this; }
            public Builder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }

            public DashboardSummaryDto build() {
                return new DashboardSummaryDto(activeExpeditionsCount, activeExpeditions, cargoSummary, inventoryAlerts, headcountBreakdown, activeEmergenciesCount, activeEmergencies, generatedAt);
            }
        }
    }

    public static class CargoSummary {
        private long inTransitCount;
        private long packedOrDispatchedCount;
        private long arrivedOrStoredCount;
        private double totalCargoWeightInTransitKg;

        public CargoSummary() {}
        public CargoSummary(long inTransitCount, long packedOrDispatchedCount, long arrivedOrStoredCount, double totalCargoWeightInTransitKg) {
            this.inTransitCount = inTransitCount;
            this.packedOrDispatchedCount = packedOrDispatchedCount;
            this.arrivedOrStoredCount = arrivedOrStoredCount;
            this.totalCargoWeightInTransitKg = totalCargoWeightInTransitKg;
        }

        public long getInTransitCount() { return inTransitCount; }
        public void setInTransitCount(long inTransitCount) { this.inTransitCount = inTransitCount; }
        public long getPackedOrDispatchedCount() { return packedOrDispatchedCount; }
        public void setPackedOrDispatchedCount(long packedOrDispatchedCount) { this.packedOrDispatchedCount = packedOrDispatchedCount; }
        public long getArrivedOrStoredCount() { return arrivedOrStoredCount; }
        public void setArrivedOrStoredCount(long arrivedOrStoredCount) { this.arrivedOrStoredCount = arrivedOrStoredCount; }
        public double getTotalCargoWeightInTransitKg() { return totalCargoWeightInTransitKg; }
        public void setTotalCargoWeightInTransitKg(double totalCargoWeightInTransitKg) { this.totalCargoWeightInTransitKg = totalCargoWeightInTransitKg; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private long inTransitCount;
            private long packedOrDispatchedCount;
            private long arrivedOrStoredCount;
            private double totalCargoWeightInTransitKg;

            public Builder inTransitCount(long inTransitCount) { this.inTransitCount = inTransitCount; return this; }
            public Builder packedOrDispatchedCount(long packedOrDispatchedCount) { this.packedOrDispatchedCount = packedOrDispatchedCount; return this; }
            public Builder arrivedOrStoredCount(long arrivedOrStoredCount) { this.arrivedOrStoredCount = arrivedOrStoredCount; return this; }
            public Builder totalCargoWeightInTransitKg(double totalCargoWeightInTransitKg) { this.totalCargoWeightInTransitKg = totalCargoWeightInTransitKg; return this; }

            public CargoSummary build() {
                return new CargoSummary(inTransitCount, packedOrDispatchedCount, arrivedOrStoredCount, totalCargoWeightInTransitKg);
            }
        }
    }
}
