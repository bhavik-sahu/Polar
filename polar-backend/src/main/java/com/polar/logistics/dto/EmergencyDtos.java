package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.EmergencySeverity;
import com.polar.logistics.entity.enums.EmergencyStatus;
import com.polar.logistics.entity.enums.EmergencyTriggerType;
import com.polar.logistics.entity.enums.StationName;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EmergencyDtos {

    public static class ManualSosRequest {
        private UUID personId;
        private UUID expeditionId;
        private StationName station;
        private EmergencySeverity severity;
        private Double latitude;
        private Double longitude;
        private String note;

        public ManualSosRequest() {}
        public ManualSosRequest(UUID personId, UUID expeditionId, StationName station, EmergencySeverity severity, Double latitude, Double longitude, String note) {
            this.personId = personId;
            this.expeditionId = expeditionId;
            this.station = station;
            this.severity = severity;
            this.latitude = latitude;
            this.longitude = longitude;
            this.note = note;
        }

        public UUID getPersonId() { return personId; }
        public void setPersonId(UUID personId) { this.personId = personId; }
        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
        public StationName getStation() { return station; }
        public void setStation(StationName station) { this.station = station; }
        public EmergencySeverity getSeverity() { return severity; }
        public void setSeverity(EmergencySeverity severity) { this.severity = severity; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID personId;
            private UUID expeditionId;
            private StationName station;
            private EmergencySeverity severity;
            private Double latitude;
            private Double longitude;
            private String note;

            public Builder personId(UUID personId) { this.personId = personId; return this; }
            public Builder expeditionId(UUID expeditionId) { this.expeditionId = expeditionId; return this; }
            public Builder station(StationName station) { this.station = station; return this; }
            public Builder severity(EmergencySeverity severity) { this.severity = severity; return this; }
            public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
            public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
            public Builder note(String note) { this.note = note; return this; }

            public ManualSosRequest build() {
                return new ManualSosRequest(personId, expeditionId, station, severity, latitude, longitude, note);
            }
        }
    }

    public static class ResolveEmergencyRequest {
        @NotBlank(message = "Resolution notes are required")
        private String resolutionNotes;

        public ResolveEmergencyRequest() {}
        public ResolveEmergencyRequest(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String resolutionNotes;
            public Builder resolutionNotes(String notes) { this.resolutionNotes = notes; return this; }
            public ResolveEmergencyRequest build() { return new ResolveEmergencyRequest(resolutionNotes); }
        }
    }

    public static class EmergencyResponseLogRequest {
        @NotBlank(message = "Action taken description is required")
        private String actionTaken;

        public EmergencyResponseLogRequest() {}
        public EmergencyResponseLogRequest(String actionTaken) { this.actionTaken = actionTaken; }

        public String getActionTaken() { return actionTaken; }
        public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String actionTaken;
            public Builder actionTaken(String action) { this.actionTaken = action; return this; }
            public EmergencyResponseLogRequest build() { return new EmergencyResponseLogRequest(actionTaken); }
        }
    }

    public static class EmergencyResponseLogDto {
        private UUID id;
        private String actionTaken;
        private UUID performedById;
        private String performedByName;
        private LocalDateTime timestamp;

        public EmergencyResponseLogDto() {}
        public EmergencyResponseLogDto(UUID id, String actionTaken, UUID performedById, String performedByName, LocalDateTime timestamp) {
            this.id = id;
            this.actionTaken = actionTaken;
            this.performedById = performedById;
            this.performedByName = performedByName;
            this.timestamp = timestamp;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getActionTaken() { return actionTaken; }
        public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
        public UUID getPerformedById() { return performedById; }
        public void setPerformedById(UUID performedById) { this.performedById = performedById; }
        public String getPerformedByName() { return performedByName; }
        public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String actionTaken;
            private UUID performedById;
            private String performedByName;
            private LocalDateTime timestamp;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder actionTaken(String actionTaken) { this.actionTaken = actionTaken; return this; }
            public Builder performedById(UUID performedById) { this.performedById = performedById; return this; }
            public Builder performedByName(String performedByName) { this.performedByName = performedByName; return this; }
            public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

            public EmergencyResponseLogDto build() {
                return new EmergencyResponseLogDto(id, actionTaken, performedById, performedByName, timestamp);
            }
        }
    }

    public static class EmergencyIncidentDto {
        private UUID id;
        private EmergencyTriggerType triggerType;
        private UUID triggeredByPersonId;
        private String triggeredByPersonName;
        private UUID relatedExpeditionId;
        private String relatedExpeditionName;
        private StationName station;
        private EmergencySeverity severity;
        private EmergencyStatus status;
        private Double lastKnownLatitude;
        private Double lastKnownLongitude;
        private LocalDateTime triggeredAt;
        private LocalDateTime resolvedAt;
        private String resolutionNotes;
        private List<EmergencyResponseLogDto> responseLogs;

        public EmergencyIncidentDto() {}
        public EmergencyIncidentDto(UUID id, EmergencyTriggerType triggerType, UUID triggeredByPersonId,
                                    String triggeredByPersonName, UUID relatedExpeditionId, String relatedExpeditionName,
                                    StationName station, EmergencySeverity severity, EmergencyStatus status,
                                    Double lastKnownLatitude, Double lastKnownLongitude, LocalDateTime triggeredAt,
                                    LocalDateTime resolvedAt, String resolutionNotes, List<EmergencyResponseLogDto> responseLogs) {
            this.id = id;
            this.triggerType = triggerType;
            this.triggeredByPersonId = triggeredByPersonId;
            this.triggeredByPersonName = triggeredByPersonName;
            this.relatedExpeditionId = relatedExpeditionId;
            this.relatedExpeditionName = relatedExpeditionName;
            this.station = station;
            this.severity = severity;
            this.status = status;
            this.lastKnownLatitude = lastKnownLatitude;
            this.lastKnownLongitude = lastKnownLongitude;
            this.triggeredAt = triggeredAt;
            this.resolvedAt = resolvedAt;
            this.resolutionNotes = resolutionNotes;
            this.responseLogs = responseLogs;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public EmergencyTriggerType getTriggerType() { return triggerType; }
        public void setTriggerType(EmergencyTriggerType triggerType) { this.triggerType = triggerType; }
        public UUID getTriggeredByPersonId() { return triggeredByPersonId; }
        public void setTriggeredByPersonId(UUID triggeredByPersonId) { this.triggeredByPersonId = triggeredByPersonId; }
        public String getTriggeredByPersonName() { return triggeredByPersonName; }
        public void setTriggeredByPersonName(String triggeredByPersonName) { this.triggeredByPersonName = triggeredByPersonName; }
        public UUID getRelatedExpeditionId() { return relatedExpeditionId; }
        public void setRelatedExpeditionId(UUID relatedExpeditionId) { this.relatedExpeditionId = relatedExpeditionId; }
        public String getRelatedExpeditionName() { return relatedExpeditionName; }
        public void setRelatedExpeditionName(String relatedExpeditionName) { this.relatedExpeditionName = relatedExpeditionName; }
        public StationName getStation() { return station; }
        public void setStation(StationName station) { this.station = station; }
        public EmergencySeverity getSeverity() { return severity; }
        public void setSeverity(EmergencySeverity severity) { this.severity = severity; }
        public EmergencyStatus getStatus() { return status; }
        public void setStatus(EmergencyStatus status) { this.status = status; }
        public Double getLastKnownLatitude() { return lastKnownLatitude; }
        public void setLastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; }
        public Double getLastKnownLongitude() { return lastKnownLongitude; }
        public void setLastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; }
        public LocalDateTime getTriggeredAt() { return triggeredAt; }
        public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
        public LocalDateTime getResolvedAt() { return resolvedAt; }
        public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
        public List<EmergencyResponseLogDto> getResponseLogs() { return responseLogs; }
        public void setResponseLogs(List<EmergencyResponseLogDto> responseLogs) { this.responseLogs = responseLogs; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private EmergencyTriggerType triggerType;
            private UUID triggeredByPersonId;
            private String triggeredByPersonName;
            private UUID relatedExpeditionId;
            private String relatedExpeditionName;
            private StationName station;
            private EmergencySeverity severity;
            private EmergencyStatus status;
            private Double lastKnownLatitude;
            private Double lastKnownLongitude;
            private LocalDateTime triggeredAt;
            private LocalDateTime resolvedAt;
            private String resolutionNotes;
            private List<EmergencyResponseLogDto> responseLogs;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder triggerType(EmergencyTriggerType triggerType) { this.triggerType = triggerType; return this; }
            public Builder triggeredByPersonId(UUID triggeredByPersonId) { this.triggeredByPersonId = triggeredByPersonId; return this; }
            public Builder triggeredByPersonName(String triggeredByPersonName) { this.triggeredByPersonName = triggeredByPersonName; return this; }
            public Builder relatedExpeditionId(UUID relatedExpeditionId) { this.relatedExpeditionId = relatedExpeditionId; return this; }
            public Builder relatedExpeditionName(String relatedExpeditionName) { this.relatedExpeditionName = relatedExpeditionName; return this; }
            public Builder station(StationName station) { this.station = station; return this; }
            public Builder severity(EmergencySeverity severity) { this.severity = severity; return this; }
            public Builder status(EmergencyStatus status) { this.status = status; return this; }
            public Builder lastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; return this; }
            public Builder lastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; return this; }
            public Builder triggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; return this; }
            public Builder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
            public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
            public Builder responseLogs(List<EmergencyResponseLogDto> responseLogs) { this.responseLogs = responseLogs; return this; }

            public EmergencyIncidentDto build() {
                return new EmergencyIncidentDto(id, triggerType, triggeredByPersonId, triggeredByPersonName, relatedExpeditionId, relatedExpeditionName, station, severity, status, lastKnownLatitude, lastKnownLongitude, triggeredAt, resolvedAt, resolutionNotes, responseLogs);
            }
        }
    }
}
