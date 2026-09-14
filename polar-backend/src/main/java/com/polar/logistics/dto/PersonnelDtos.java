package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.entity.enums.PersonRole;
import com.polar.logistics.entity.enums.PersonStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class PersonnelDtos {

    public static class CreatePersonRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Role is required")
        private PersonRole role;

        private FitnessClearanceStatus fitnessClearanceStatus;
        private PersonStatus currentStatus;
        private String currentLocation;
        private UUID expeditionId;
        private UUID currentTransitLegId;

        public CreatePersonRequest() {}
        public CreatePersonRequest(String name, PersonRole role, FitnessClearanceStatus fitnessClearanceStatus,
                                   PersonStatus currentStatus, String currentLocation, UUID expeditionId, UUID currentTransitLegId) {
            this.name = name;
            this.role = role;
            this.fitnessClearanceStatus = fitnessClearanceStatus;
            this.currentStatus = currentStatus;
            this.currentLocation = currentLocation;
            this.expeditionId = expeditionId;
            this.currentTransitLegId = currentTransitLegId;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public PersonRole getRole() { return role; }
        public void setRole(PersonRole role) { this.role = role; }
        public FitnessClearanceStatus getFitnessClearanceStatus() { return fitnessClearanceStatus; }
        public void setFitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; }
        public PersonStatus getCurrentStatus() { return currentStatus; }
        public void setCurrentStatus(PersonStatus currentStatus) { this.currentStatus = currentStatus; }
        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
        public UUID getCurrentTransitLegId() { return currentTransitLegId; }
        public void setCurrentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private PersonRole role;
            private FitnessClearanceStatus fitnessClearanceStatus;
            private PersonStatus currentStatus;
            private String currentLocation;
            private UUID expeditionId;
            private UUID currentTransitLegId;

            public Builder name(String name) { this.name = name; return this; }
            public Builder role(PersonRole role) { this.role = role; return this; }
            public Builder fitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; return this; }
            public Builder currentStatus(PersonStatus currentStatus) { this.currentStatus = currentStatus; return this; }
            public Builder currentLocation(String currentLocation) { this.currentLocation = currentLocation; return this; }
            public Builder expeditionId(UUID expeditionId) { this.expeditionId = expeditionId; return this; }
            public Builder currentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; return this; }

            public CreatePersonRequest build() {
                return new CreatePersonRequest(name, role, fitnessClearanceStatus, currentStatus, currentLocation, expeditionId, currentTransitLegId);
            }
        }
    }

    public static class AssignExpeditionRequest {
        @NotNull(message = "Expedition ID is required")
        private UUID expeditionId;

        public AssignExpeditionRequest() {}
        public AssignExpeditionRequest(UUID expeditionId) { this.expeditionId = expeditionId; }

        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
    }

    public static class UpdatePersonStatusRequest {
        @NotNull(message = "Current status is required")
        private PersonStatus currentStatus;

        private String currentLocation;
        private UUID currentTransitLegId;

        public UpdatePersonStatusRequest() {}
        public UpdatePersonStatusRequest(PersonStatus currentStatus, String currentLocation, UUID currentTransitLegId) {
            this.currentStatus = currentStatus;
            this.currentLocation = currentLocation;
            this.currentTransitLegId = currentTransitLegId;
        }

        public PersonStatus getCurrentStatus() { return currentStatus; }
        public void setCurrentStatus(PersonStatus currentStatus) { this.currentStatus = currentStatus; }
        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
        public UUID getCurrentTransitLegId() { return currentTransitLegId; }
        public void setCurrentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; }
    }

    public static class UpdateFitnessStatusRequest {
        @NotNull(message = "Fitness clearance status is required")
        private FitnessClearanceStatus fitnessClearanceStatus;

        public UpdateFitnessStatusRequest() {}
        public UpdateFitnessStatusRequest(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; }

        public FitnessClearanceStatus getFitnessClearanceStatus() { return fitnessClearanceStatus; }
        public void setFitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; }
    }

    public static class PersonDto {
        private UUID id;
        private String name;
        private PersonRole role;
        private FitnessClearanceStatus fitnessClearanceStatus;
        private PersonStatus currentStatus;
        private String currentLocation;
        private UUID currentTransitLegId;
        private String currentTransitLegInfo;
        private UUID expeditionId;
        private String expeditionName;
        private Double lastKnownLatitude;
        private Double lastKnownLongitude;
        private LocalDateTime lastPingTime;

        public PersonDto() {}
        public PersonDto(UUID id, String name, PersonRole role, FitnessClearanceStatus fitnessClearanceStatus,
                         PersonStatus currentStatus, String currentLocation, UUID currentTransitLegId,
                         String currentTransitLegInfo, UUID expeditionId, String expeditionName,
                         Double lastKnownLatitude, Double lastKnownLongitude, LocalDateTime lastPingTime) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.fitnessClearanceStatus = fitnessClearanceStatus;
            this.currentStatus = currentStatus;
            this.currentLocation = currentLocation;
            this.currentTransitLegId = currentTransitLegId;
            this.currentTransitLegInfo = currentTransitLegInfo;
            this.expeditionId = expeditionId;
            this.expeditionName = expeditionName;
            this.lastKnownLatitude = lastKnownLatitude;
            this.lastKnownLongitude = lastKnownLongitude;
            this.lastPingTime = lastPingTime;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public PersonRole getRole() { return role; }
        public void setRole(PersonRole role) { this.role = role; }
        public FitnessClearanceStatus getFitnessClearanceStatus() { return fitnessClearanceStatus; }
        public void setFitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; }
        public PersonStatus getCurrentStatus() { return currentStatus; }
        public void setCurrentStatus(PersonStatus currentStatus) { this.currentStatus = currentStatus; }
        public String getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
        public UUID getCurrentTransitLegId() { return currentTransitLegId; }
        public void setCurrentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; }
        public String getCurrentTransitLegInfo() { return currentTransitLegInfo; }
        public void setCurrentTransitLegInfo(String currentTransitLegInfo) { this.currentTransitLegInfo = currentTransitLegInfo; }
        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
        public String getExpeditionName() { return expeditionName; }
        public void setExpeditionName(String expeditionName) { this.expeditionName = expeditionName; }
        public Double getLastKnownLatitude() { return lastKnownLatitude; }
        public void setLastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; }
        public Double getLastKnownLongitude() { return lastKnownLongitude; }
        public void setLastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; }
        public LocalDateTime getLastPingTime() { return lastPingTime; }
        public void setLastPingTime(LocalDateTime lastPingTime) { this.lastPingTime = lastPingTime; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String name;
            private PersonRole role;
            private FitnessClearanceStatus fitnessClearanceStatus;
            private PersonStatus currentStatus;
            private String currentLocation;
            private UUID currentTransitLegId;
            private String currentTransitLegInfo;
            private UUID expeditionId;
            private String expeditionName;
            private Double lastKnownLatitude;
            private Double lastKnownLongitude;
            private LocalDateTime lastPingTime;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder role(PersonRole role) { this.role = role; return this; }
            public Builder fitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; return this; }
            public Builder currentStatus(PersonStatus currentStatus) { this.currentStatus = currentStatus; return this; }
            public Builder currentLocation(String currentLocation) { this.currentLocation = currentLocation; return this; }
            public Builder currentTransitLegId(UUID currentTransitLegId) { this.currentTransitLegId = currentTransitLegId; return this; }
            public Builder currentTransitLegInfo(String currentTransitLegInfo) { this.currentTransitLegInfo = currentTransitLegInfo; return this; }
            public Builder expeditionId(UUID expeditionId) { this.expeditionId = expeditionId; return this; }
            public Builder expeditionName(String expeditionName) { this.expeditionName = expeditionName; return this; }
            public Builder lastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; return this; }
            public Builder lastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; return this; }
            public Builder lastPingTime(LocalDateTime lastPingTime) { this.lastPingTime = lastPingTime; return this; }

            public PersonDto build() {
                return new PersonDto(id, name, role, fitnessClearanceStatus, currentStatus, currentLocation, currentTransitLegId, currentTransitLegInfo, expeditionId, expeditionName, lastKnownLatitude, lastKnownLongitude, lastPingTime);
            }
        }
    }

    public static class HeadcountSummaryDto {
        private Map<String, Map<String, Long>> stationBreakdown;
        private long totalPersonnel;

        public HeadcountSummaryDto() {}
        public HeadcountSummaryDto(Map<String, Map<String, Long>> stationBreakdown, long totalPersonnel) {
            this.stationBreakdown = stationBreakdown;
            this.totalPersonnel = totalPersonnel;
        }

        public Map<String, Map<String, Long>> getStationBreakdown() { return stationBreakdown; }
        public void setStationBreakdown(Map<String, Map<String, Long>> stationBreakdown) { this.stationBreakdown = stationBreakdown; }
        public long getTotalPersonnel() { return totalPersonnel; }
        public void setTotalPersonnel(long totalPersonnel) { this.totalPersonnel = totalPersonnel; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Map<String, Map<String, Long>> stationBreakdown;
            private long totalPersonnel;

            public Builder stationBreakdown(Map<String, Map<String, Long>> stationBreakdown) { this.stationBreakdown = stationBreakdown; return this; }
            public Builder totalPersonnel(long totalPersonnel) { this.totalPersonnel = totalPersonnel; return this; }

            public HeadcountSummaryDto build() {
                return new HeadcountSummaryDto(stationBreakdown, totalPersonnel);
            }
        }
    }
}
