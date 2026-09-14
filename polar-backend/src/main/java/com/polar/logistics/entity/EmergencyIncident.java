package com.polar.logistics.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.polar.logistics.entity.enums.EmergencySeverity;
import com.polar.logistics.entity.enums.EmergencyStatus;
import com.polar.logistics.entity.enums.EmergencyTriggerType;
import com.polar.logistics.entity.enums.StationName;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "emergency_incidents")
public class EmergencyIncident {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private EmergencyTriggerType triggerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by_person_id")
    private Person triggeredBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_expedition_id")
    private Expedition relatedExpedition;

    @Enumerated(EnumType.STRING)
    @Column(name = "station")
    private StationName station;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private EmergencySeverity severity = EmergencySeverity.HIGH;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmergencyStatus status = EmergencyStatus.ACTIVE;

    @Column(name = "last_known_latitude")
    private Double lastKnownLatitude;

    @Column(name = "last_known_longitude")
    private Double lastKnownLongitude;

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp DESC")
    @JsonManagedReference
    private List<EmergencyResponseLog> responseLogs = new ArrayList<>();

    public EmergencyIncident() {}

    public EmergencyIncident(UUID id, EmergencyTriggerType triggerType, Person triggeredBy, Expedition relatedExpedition,
                             StationName station, EmergencySeverity severity, EmergencyStatus status,
                             Double lastKnownLatitude, Double lastKnownLongitude, LocalDateTime triggeredAt,
                             LocalDateTime resolvedAt, String resolutionNotes, List<EmergencyResponseLog> responseLogs) {
        this.id = id;
        this.triggerType = triggerType;
        this.triggeredBy = triggeredBy;
        this.relatedExpedition = relatedExpedition;
        this.station = station;
        this.severity = severity != null ? severity : EmergencySeverity.HIGH;
        this.status = status != null ? status : EmergencyStatus.ACTIVE;
        this.lastKnownLatitude = lastKnownLatitude;
        this.lastKnownLongitude = lastKnownLongitude;
        this.triggeredAt = triggeredAt != null ? triggeredAt : LocalDateTime.now();
        this.resolvedAt = resolvedAt;
        this.resolutionNotes = resolutionNotes;
        this.responseLogs = responseLogs != null ? responseLogs : new ArrayList<>();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public EmergencyTriggerType getTriggerType() { return triggerType; }
    public void setTriggerType(EmergencyTriggerType triggerType) { this.triggerType = triggerType; }

    public Person getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(Person triggeredBy) { this.triggeredBy = triggeredBy; }

    public Expedition getRelatedExpedition() { return relatedExpedition; }
    public void setRelatedExpedition(Expedition relatedExpedition) { this.relatedExpedition = relatedExpedition; }

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

    public List<EmergencyResponseLog> getResponseLogs() { return responseLogs; }
    public void setResponseLogs(List<EmergencyResponseLog> responseLogs) { this.responseLogs = responseLogs; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private EmergencyTriggerType triggerType;
        private Person triggeredBy;
        private Expedition relatedExpedition;
        private StationName station;
        private EmergencySeverity severity = EmergencySeverity.HIGH;
        private EmergencyStatus status = EmergencyStatus.ACTIVE;
        private Double lastKnownLatitude;
        private Double lastKnownLongitude;
        private LocalDateTime triggeredAt = LocalDateTime.now();
        private LocalDateTime resolvedAt;
        private String resolutionNotes;
        private List<EmergencyResponseLog> responseLogs = new ArrayList<>();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder triggerType(EmergencyTriggerType triggerType) { this.triggerType = triggerType; return this; }
        public Builder triggeredBy(Person triggeredBy) { this.triggeredBy = triggeredBy; return this; }
        public Builder relatedExpedition(Expedition relatedExpedition) { this.relatedExpedition = relatedExpedition; return this; }
        public Builder station(StationName station) { this.station = station; return this; }
        public Builder severity(EmergencySeverity severity) { this.severity = severity; return this; }
        public Builder status(EmergencyStatus status) { this.status = status; return this; }
        public Builder lastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; return this; }
        public Builder lastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; return this; }
        public Builder triggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; return this; }
        public Builder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public Builder resolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; return this; }
        public Builder responseLogs(List<EmergencyResponseLog> responseLogs) { this.responseLogs = responseLogs; return this; }

        public EmergencyIncident build() {
            return new EmergencyIncident(id, triggerType, triggeredBy, relatedExpedition, station, severity, status, lastKnownLatitude, lastKnownLongitude, triggeredAt, resolvedAt, resolutionNotes, responseLogs);
        }
    }
}
