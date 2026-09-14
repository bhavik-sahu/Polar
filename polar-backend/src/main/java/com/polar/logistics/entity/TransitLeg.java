package com.polar.logistics.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.polar.logistics.entity.enums.TransitMode;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transit_legs")
public class TransitLeg {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expedition_id", nullable = false)
    @JsonBackReference
    private Expedition expedition;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private TransitMode mode;

    @Column(name = "expected_departure", nullable = false)
    private LocalDateTime expectedDeparture;

    @Column(name = "expected_arrival", nullable = false)
    private LocalDateTime expectedArrival;

    @Column(name = "actual_departure")
    private LocalDateTime actualDeparture;

    @Column(name = "actual_arrival")
    private LocalDateTime actualArrival;

    @Column(name = "max_weight_capacity_kg", precision = 12, scale = 2)
    private BigDecimal maxWeightCapacityKg;

    @Column(name = "max_volume_capacity_m3", precision = 10, scale = 2)
    private BigDecimal maxVolumeCapacityM3;

    public TransitLeg() {}

    public TransitLeg(UUID id, Expedition expedition, Integer sequenceOrder, String origin, String destination,
                      TransitMode mode, LocalDateTime expectedDeparture, LocalDateTime expectedArrival,
                      LocalDateTime actualDeparture, LocalDateTime actualArrival,
                      BigDecimal maxWeightCapacityKg, BigDecimal maxVolumeCapacityM3) {
        this.id = id;
        this.expedition = expedition;
        this.sequenceOrder = sequenceOrder;
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
        this.expectedDeparture = expectedDeparture;
        this.expectedArrival = expectedArrival;
        this.actualDeparture = actualDeparture;
        this.actualArrival = actualArrival;
        this.maxWeightCapacityKg = maxWeightCapacityKg;
        this.maxVolumeCapacityM3 = maxVolumeCapacityM3;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Expedition getExpedition() { return expedition; }
    public void setExpedition(Expedition expedition) { this.expedition = expedition; }

    public Integer getSequenceOrder() { return sequenceOrder; }
    public void setSequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public TransitMode getMode() { return mode; }
    public void setMode(TransitMode mode) { this.mode = mode; }

    public LocalDateTime getExpectedDeparture() { return expectedDeparture; }
    public void setExpectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; }

    public LocalDateTime getExpectedArrival() { return expectedArrival; }
    public void setExpectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; }

    public LocalDateTime getActualDeparture() { return actualDeparture; }
    public void setActualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; }

    public LocalDateTime getActualArrival() { return actualArrival; }
    public void setActualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; }

    public BigDecimal getMaxWeightCapacityKg() { return maxWeightCapacityKg; }
    public void setMaxWeightCapacityKg(BigDecimal maxWeightCapacityKg) { this.maxWeightCapacityKg = maxWeightCapacityKg; }

    public BigDecimal getMaxVolumeCapacityM3() { return maxVolumeCapacityM3; }
    public void setMaxVolumeCapacityM3(BigDecimal maxVolumeCapacityM3) { this.maxVolumeCapacityM3 = maxVolumeCapacityM3; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private Expedition expedition;
        private Integer sequenceOrder;
        private String origin;
        private String destination;
        private TransitMode mode;
        private LocalDateTime expectedDeparture;
        private LocalDateTime expectedArrival;
        private LocalDateTime actualDeparture;
        private LocalDateTime actualArrival;
        private BigDecimal maxWeightCapacityKg;
        private BigDecimal maxVolumeCapacityM3;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder expedition(Expedition expedition) { this.expedition = expedition; return this; }
        public Builder sequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; return this; }
        public Builder origin(String origin) { this.origin = origin; return this; }
        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder mode(TransitMode mode) { this.mode = mode; return this; }
        public Builder expectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; return this; }
        public Builder expectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; return this; }
        public Builder actualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; return this; }
        public Builder actualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; return this; }
        public Builder maxWeightCapacityKg(BigDecimal maxWeightCapacityKg) { this.maxWeightCapacityKg = maxWeightCapacityKg; return this; }
        public Builder maxVolumeCapacityM3(BigDecimal maxVolumeCapacityM3) { this.maxVolumeCapacityM3 = maxVolumeCapacityM3; return this; }

        public TransitLeg build() {
            return new TransitLeg(id, expedition, sequenceOrder, origin, destination, mode, expectedDeparture, expectedArrival, actualDeparture, actualArrival, maxWeightCapacityKg, maxVolumeCapacityM3);
        }
    }
}
