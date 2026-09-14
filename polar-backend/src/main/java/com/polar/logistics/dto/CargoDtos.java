package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.CargoCategory;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.entity.enums.StationLocation;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CargoDtos {

    public static class CreateCargoRequest {
        @NotBlank(message = "Cargo name is required")
        private String name;

        @NotNull(message = "Category is required")
        private CargoCategory category;

        @NotNull(message = "Weight in kg is required")
        @DecimalMin(value = "0.01", message = "Weight must be greater than 0")
        private BigDecimal weightKg;

        private UUID expeditionId;
        private UUID currentTransitLegId;
        private StationLocation destinationStation;

        public CreateCargoRequest() {}
        public CreateCargoRequest(String name, CargoCategory category, BigDecimal weightKg, UUID expeditionId, UUID currentTransitLegId, StationLocation destinationStation) {
            this.name = name;
            this.category = category;
            this.weightKg = weightKg;
            this.expeditionId = expeditionId;
            this.currentTransitLegId = currentTransitLegId;
            this.destinationStation = destinationStation;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public CargoCategory getCategory() { return category; }
        public void setCategory(CargoCategory category) { this.category = category; }
        public BigDecimal getWeightKg() { return weightKg; }
        public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
        public UUID getCurrentTransitLegId() { return currentTransitLegId; }
        public void setCurrentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; }
        public StationLocation getDestinationStation() { return destinationStation; }
        public void setDestinationStation(StationLocation destinationStation) { this.destinationStation = destinationStation; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private CargoCategory category;
            private BigDecimal weightKg;
            private UUID expeditionId;
            private UUID currentTransitLegId;
            private StationLocation destinationStation;

            public Builder name(String name) { this.name = name; return this; }
            public Builder category(CargoCategory category) { this.category = category; return this; }
            public Builder weightKg(BigDecimal weightKg) { this.weightKg = weightKg; return this; }
            public Builder expeditionId(UUID expeditionId) { this.expeditionId = expeditionId; return this; }
            public Builder currentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; return this; }
            public Builder destinationStation(StationLocation destinationStation) { this.destinationStation = destinationStation; return this; }

            public CreateCargoRequest build() {
                return new CreateCargoRequest(name, category, weightKg, expeditionId, currentTransitLegId, destinationStation);
            }
        }
    }

    public static class UpdateCargoStatusRequest {
        @NotNull(message = "Status is required")
        private CargoStatus status;

        private StationLocation stationLocation;
        private UUID currentTransitLegId;

        public UpdateCargoStatusRequest() {}
        public UpdateCargoStatusRequest(CargoStatus status, StationLocation stationLocation, UUID currentTransitLegId) {
            this.status = status;
            this.stationLocation = stationLocation;
            this.currentTransitLegId = currentTransitLegId;
        }

        public CargoStatus getStatus() { return status; }
        public void setStatus(CargoStatus status) { this.status = status; }
        public StationLocation getStationLocation() { return stationLocation; }
        public void setStationLocation(StationLocation stationLocation) { this.stationLocation = stationLocation; }
        public UUID getCurrentTransitLegId() { return currentTransitLegId; }
        public void setCurrentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private CargoStatus status;
            private StationLocation stationLocation;
            private UUID currentTransitLegId;

            public Builder status(CargoStatus status) { this.status = status; return this; }
            public Builder stationLocation(StationLocation stationLocation) { this.stationLocation = stationLocation; return this; }
            public Builder currentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; return this; }

            public UpdateCargoStatusRequest build() {
                return new UpdateCargoStatusRequest(status, stationLocation, currentTransitLegId);
            }
        }
    }

    public static class AssignCargoRequest {
        @NotNull(message = "Expedition ID is required")
        private UUID expeditionId;

        public AssignCargoRequest() {}
        public AssignCargoRequest(UUID expeditionId) { this.expeditionId = expeditionId; }

        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
    }

    public static class CargoItemDto {
        private UUID id;
        private String name;
        private CargoCategory category;
        private BigDecimal weightKg;
        private CargoStatus status;
        private UUID expeditionId;
        private String expeditionName;
        private UUID currentTransitLegId;
        private String currentTransitLegInfo;
        private StationLocation currentStationLocation;
        private UUID linkedInventoryItemId;
        private Double lastKnownLatitude;
        private Double lastKnownLongitude;
        private LocalDateTime lastPingTime;
        private String warning;

        public CargoItemDto() {}
        public CargoItemDto(UUID id, String name, CargoCategory category, BigDecimal weightKg, CargoStatus status,
                            UUID expeditionId, String expeditionName, UUID currentTransitLegId,
                            String currentTransitLegInfo, StationLocation currentStationLocation,
                            UUID linkedInventoryItemId, Double lastKnownLatitude, Double lastKnownLongitude,
                            LocalDateTime lastPingTime, String warning) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.weightKg = weightKg;
            this.status = status;
            this.expeditionId = expeditionId;
            this.expeditionName = expeditionName;
            this.currentTransitLegId = currentTransitLegId;
            this.currentTransitLegInfo = currentTransitLegInfo;
            this.currentStationLocation = currentStationLocation;
            this.linkedInventoryItemId = linkedInventoryItemId;
            this.lastKnownLatitude = lastKnownLatitude;
            this.lastKnownLongitude = lastKnownLongitude;
            this.lastPingTime = lastPingTime;
            this.warning = warning;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public CargoCategory getCategory() { return category; }
        public void setCategory(CargoCategory category) { this.category = category; }
        public BigDecimal getWeightKg() { return weightKg; }
        public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
        public CargoStatus getStatus() { return status; }
        public void setStatus(CargoStatus status) { this.status = status; }
        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
        public String getExpeditionName() { return expeditionName; }
        public void setExpeditionName(String expeditionName) { this.expeditionName = expeditionName; }
        public UUID getCurrentTransitLegId() { return currentTransitLegId; }
        public void setCurrentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; }
        public String getCurrentTransitLegInfo() { return currentTransitLegInfo; }
        public void setCurrentTransitLegInfo(String currentTransitLegInfo) { this.currentTransitLegInfo = currentTransitLegInfo; }
        public StationLocation getCurrentStationLocation() { return currentStationLocation; }
        public void setCurrentStationLocation(StationLocation currentStationLocation) { this.currentStationLocation = currentStationLocation; }
        public UUID getLinkedInventoryItemId() { return linkedInventoryItemId; }
        public void setLinkedInventoryItemId(UUID linkedInventoryItemId) { this.linkedInventoryItemId = linkedInventoryItemId; }
        public Double getLastKnownLatitude() { return lastKnownLatitude; }
        public void setLastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; }
        public Double getLastKnownLongitude() { return lastKnownLongitude; }
        public void setLastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; }
        public LocalDateTime getLastPingTime() { return lastPingTime; }
        public void setLastPingTime(LocalDateTime lastPingTime) { this.lastPingTime = lastPingTime; }
        public String getWarning() { return warning; }
        public void setWarning(String warning) { this.warning = warning; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String name;
            private CargoCategory category;
            private BigDecimal weightKg;
            private CargoStatus status;
            private UUID expeditionId;
            private String expeditionName;
            private UUID currentTransitLegId;
            private String currentTransitLegInfo;
            private StationLocation currentStationLocation;
            private UUID linkedInventoryItemId;
            private Double lastKnownLatitude;
            private Double lastKnownLongitude;
            private LocalDateTime lastPingTime;
            private String warning;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder category(CargoCategory category) { this.category = category; return this; }
            public Builder weightKg(BigDecimal weightKg) { this.weightKg = weightKg; return this; }
            public Builder status(CargoStatus status) { this.status = status; return this; }
            public Builder expeditionId(UUID expeditionId) { this.expeditionId = expeditionId; return this; }
            public Builder expeditionName(String expeditionName) { this.expeditionName = expeditionName; return this; }
            public Builder currentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; return this; }
            public Builder currentTransitLegInfo(String currentTransitLegInfo) { this.currentTransitLegInfo = currentTransitLegInfo; return this; }
            public Builder currentStationLocation(StationLocation currentStationLocation) { this.currentStationLocation = currentStationLocation; return this; }
            public Builder linkedInventoryItemId(UUID linkedInventoryItemId) { this.linkedInventoryItemId = linkedInventoryItemId; return this; }
            public Builder lastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; return this; }
            public Builder lastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; return this; }
            public Builder lastPingTime(LocalDateTime lastPingTime) { this.lastPingTime = lastPingTime; return this; }
            public Builder warning(String warning) { this.warning = warning; return this; }

            public CargoItemDto build() {
                return new CargoItemDto(id, name, category, weightKg, status, expeditionId, expeditionName, currentTransitLegId, currentTransitLegInfo, currentStationLocation, linkedInventoryItemId, lastKnownLatitude, lastKnownLongitude, lastPingTime, warning);
            }
        }
    }
}
