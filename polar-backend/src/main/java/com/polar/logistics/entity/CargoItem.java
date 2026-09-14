package com.polar.logistics.entity;

import com.polar.logistics.entity.enums.CargoCategory;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.entity.enums.StationLocation;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cargo_items")
public class CargoItem {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CargoCategory category;

    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CargoStatus status = CargoStatus.PACKED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id")
    private Expedition expedition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_transit_leg_id")
    private TransitLeg currentTransitLeg;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_station_location")
    private StationLocation currentStationLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_inventory_item_id")
    private InventoryItem linkedInventoryItem;

    @Column(name = "last_known_latitude")
    private Double lastKnownLatitude;

    @Column(name = "last_known_longitude")
    private Double lastKnownLongitude;

    @Column(name = "last_ping_time")
    private LocalDateTime lastPingTime;

    public CargoItem() {}

    public CargoItem(UUID id, String name, CargoCategory category, BigDecimal weightKg, CargoStatus status,
                     Expedition expedition, TransitLeg currentTransitLeg, StationLocation currentStationLocation,
                     InventoryItem linkedInventoryItem, Double lastKnownLatitude, Double lastKnownLongitude, LocalDateTime lastPingTime) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.weightKg = weightKg;
        this.status = status != null ? status : CargoStatus.PACKED;
        this.expedition = expedition;
        this.currentTransitLeg = currentTransitLeg;
        this.currentStationLocation = currentStationLocation;
        this.linkedInventoryItem = linkedInventoryItem;
        this.lastKnownLatitude = lastKnownLatitude;
        this.lastKnownLongitude = lastKnownLongitude;
        this.lastPingTime = lastPingTime;
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

    public Expedition getExpedition() { return expedition; }
    public void setExpedition(Expedition expedition) { this.expedition = expedition; }

    public TransitLeg getCurrentTransitLeg() { return currentTransitLeg; }
    public void setCurrentTransitLeg(TransitLeg currentTransitLeg) { this.currentTransitLeg = currentTransitLeg; }

    public StationLocation getCurrentStationLocation() { return currentStationLocation; }
    public void setCurrentStationLocation(StationLocation currentStationLocation) { this.currentStationLocation = currentStationLocation; }

    public InventoryItem getLinkedInventoryItem() { return linkedInventoryItem; }
    public void setLinkedInventoryItem(InventoryItem linkedInventoryItem) { this.linkedInventoryItem = linkedInventoryItem; }

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
        private CargoCategory category;
        private BigDecimal weightKg;
        private CargoStatus status = CargoStatus.PACKED;
        private Expedition expedition;
        private TransitLeg currentTransitLeg;
        private StationLocation currentStationLocation;
        private InventoryItem linkedInventoryItem;
        private Double lastKnownLatitude;
        private Double lastKnownLongitude;
        private LocalDateTime lastPingTime;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder category(CargoCategory category) { this.category = category; return this; }
        public Builder weightKg(BigDecimal weightKg) { this.weightKg = weightKg; return this; }
        public Builder status(CargoStatus status) { this.status = status; return this; }
        public Builder expedition(Expedition expedition) { this.expedition = expedition; return this; }
        public Builder currentTransitLeg(TransitLeg currentTransitLeg) { this.currentTransitLeg = currentTransitLeg; return this; }
        public Builder currentStationLocation(StationLocation currentStationLocation) { this.currentStationLocation = currentStationLocation; return this; }
        public Builder linkedInventoryItem(InventoryItem linkedInventoryItem) { this.linkedInventoryItem = linkedInventoryItem; return this; }
        public Builder lastKnownLatitude(Double lastKnownLatitude) { this.lastKnownLatitude = lastKnownLatitude; return this; }
        public Builder lastKnownLongitude(Double lastKnownLongitude) { this.lastKnownLongitude = lastKnownLongitude; return this; }
        public Builder lastPingTime(LocalDateTime lastPingTime) { this.lastPingTime = lastPingTime; return this; }

        public CargoItem build() {
            return new CargoItem(id, name, category, weightKg, status, expedition, currentTransitLeg, currentStationLocation, linkedInventoryItem, lastKnownLatitude, lastKnownLongitude, lastPingTime);
        }
    }
}
