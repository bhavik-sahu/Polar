package com.polar.logistics.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergency_response_logs")
public class EmergencyResponseLog {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    @JsonBackReference
    private EmergencyIncident incident;

    @Column(name = "action_taken", nullable = false, columnDefinition = "TEXT")
    private String actionTaken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_user_id", nullable = false)
    private User performedBy;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public EmergencyResponseLog() {}

    public EmergencyResponseLog(UUID id, EmergencyIncident incident, String actionTaken, User performedBy, LocalDateTime timestamp) {
        this.id = id;
        this.incident = incident;
        this.actionTaken = actionTaken;
        this.performedBy = performedBy;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public EmergencyIncident getIncident() { return incident; }
    public void setIncident(EmergencyIncident incident) { this.incident = incident; }

    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }

    public User getPerformedBy() { return performedBy; }
    public void setPerformedBy(User performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private EmergencyIncident incident;
        private String actionTaken;
        private User performedBy;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder incident(EmergencyIncident incident) { this.incident = incident; return this; }
        public Builder actionTaken(String actionTaken) { this.actionTaken = actionTaken; return this; }
        public Builder performedBy(User performedBy) { this.performedBy = performedBy; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public EmergencyResponseLog build() {
            return new EmergencyResponseLog(id, incident, actionTaken, performedBy, timestamp);
        }
    }
}
