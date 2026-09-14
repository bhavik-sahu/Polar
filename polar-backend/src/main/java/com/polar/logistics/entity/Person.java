package com.polar.logistics.entity;

import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.entity.enums.PersonRole;
import com.polar.logistics.entity.enums.PersonStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personnel")
public class Person {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private PersonRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "fitness_clearance_status", nullable = false)
    private FitnessClearanceStatus fitnessClearanceStatus = FitnessClearanceStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false)
    private PersonStatus currentStatus = PersonStatus.IN_INDIA;

    @Column(name = "current_location")
    private String currentLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_transit_leg_id")
    private TransitLeg currentTransitLeg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    @Column(name = "last_known_latitude")
    private Double lastKnownLatitude;

    @Column(name = "last_known_longitude")
    private Double lastKnownLongitude;

    @Column(name = "last_ping_time")
    private LocalDateTime lastPingTime;

    public Person() {}

    public Person(UUID id, String name, PersonRole role, FitnessClearanceStatus fitnessClearanceStatus,
                  PersonStatus currentStatus, String currentLocation, TransitLeg currentTransitLeg,
                  Expedition expedition, Double lastKnownLatitude, Double lastKnownLongitude, LocalDateTime lastPingTime) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.fitnessClearanceStatus = fitnessClearanceStatus != null ? fitnessClearanceStatus : FitnessClearanceStatus.PENDING;
        this.currentStatus = currentStatus != null ? currentStatus : PersonStatus.IN_INDIA;
        this.currentLocation = currentLocation;
        this.currentTransitLeg = currentTransitLeg;
        this.expedition = expedition;
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

    public TransitLeg getCurrentTransitLeg() { return currentTransitLeg; }
    public void setCurrentTransitLeg(TransitLeg currentTransitLeg) { this.currentTransitLeg = currentTransitLeg; }

    public Expedition getExpedition() { return expedition; }
    public void setExpedition(Expedition expedition) { this.expedition = expedition; }

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
        private FitnessClearanceStatus fitnessClearanceStatus = FitnessClearanceStatus.PENDING;
        private PersonStatus currentStatus = PersonStatus.IN_INDIA;
        private String currentLocation;
        private TransitLeg currentTransitLeg;
        private Expedition expedition;
        private Double lastKnownLatitude;
        private Double lastKnownLongitude;
        private LocalDateTime lastPingTime;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder role(PersonRole role) { this.role = role; return this; }
        public Builder fitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; return this; }
        public Builder currentStatus(PersonStatus currentStatus) { this.currentStatus = currentStatus; return this; }
        public Builder currentLocation(String currentLocation) { this.currentLocation = currentLocation; return this; }
        public Builder currentTransitLeg(TransitLeg currentTransitLeg) { this.currentTransitLeg = currentTransitLeg; return this; }
        public Builder expedition(Expedition expedition) { this.expedition = expedition; return this; }
        public Builder lastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; return this; }
        public Builder lastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; return this; }
        public Builder lastPingTime(LocalDateTime lastPingTime) { this.lastPingTime = lastPingTime; return this; }

        public Person build() {
            return new Person(id, name, role, fitnessClearanceStatus, currentStatus, currentLocation, currentTransitLeg, expedition, lastKnownLatitude, lastKnownLongitude, lastPingTime);
        }
    }
}
