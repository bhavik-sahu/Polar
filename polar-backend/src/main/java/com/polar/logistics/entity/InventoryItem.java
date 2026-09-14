package com.polar.logistics.entity;

import com.polar.logistics.entity.enums.StationName;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "station", nullable = false)
    private StationName station;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "reorder_threshold", nullable = false)
    private Integer reorderThreshold = 10;

    @Column(name = "daily_consumption")
    private Double dailyConsumption = 0.0;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public InventoryItem() {}

    public InventoryItem(UUID id, String name, String category, StationName station, Integer quantity,
                         String unit, Integer reorderThreshold, Double dailyConsumption, LocalDate expiryDate,
                         LocalDateTime lastUpdated) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.station = station;
        this.quantity = quantity != null ? quantity : 0;
        this.unit = unit;
        this.reorderThreshold = reorderThreshold != null ? reorderThreshold : 10;
        this.dailyConsumption = dailyConsumption != null ? dailyConsumption : 0.0;
        this.expiryDate = expiryDate;
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public StationName getStation() { return station; }
    public void setStation(StationName station) { this.station = station; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Integer getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(Integer reorderThreshold) { this.reorderThreshold = reorderThreshold; }

    public Double getDailyConsumption() { return dailyConsumption; }
    public void setDailyConsumption(Double dailyConsumption) { this.dailyConsumption = dailyConsumption; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String name;
        private String category;
        private StationName station;
        private Integer quantity = 0;
        private String unit;
        private Integer reorderThreshold = 10;
        private Double dailyConsumption = 0.0;
        private LocalDate expiryDate;
        private LocalDateTime lastUpdated = LocalDateTime.now();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder station(StationName station) { this.station = station; return this; }
        public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder reorderThreshold(Integer reorderThreshold) { this.reorderThreshold = reorderThreshold; return this; }
        public Builder dailyConsumption(Double dailyConsumption) { this.dailyConsumption = dailyConsumption; return this; }
        public Builder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }
        public Builder lastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; return this; }

        public InventoryItem build() {
            return new InventoryItem(id, name, category, station, quantity, unit, reorderThreshold, dailyConsumption, expiryDate, lastUpdated);
        }
    }
}
