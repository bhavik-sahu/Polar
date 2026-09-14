package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.StationName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryDtos {

    public static class CreateInventoryItemRequest {
        @NotBlank(message = "Item name is required")
        private String name;

        @NotBlank(message = "Category is required")
        private String category;

        @NotNull(message = "Station is required")
        private StationName station;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantity;

        @NotBlank(message = "Unit is required")
        private String unit;

        @NotNull(message = "Reorder threshold is required")
        @Min(value = 0, message = "Reorder threshold cannot be negative")
        private Integer reorderThreshold;

        private Double dailyConsumption;
        private LocalDate expiryDate;

        public CreateInventoryItemRequest() {}
        public CreateInventoryItemRequest(String name, String category, StationName station, Integer quantity,
                                          String unit, Integer reorderThreshold, Double dailyConsumption, LocalDate expiryDate) {
            this.name = name;
            this.category = category;
            this.station = station;
            this.quantity = quantity;
            this.unit = unit;
            this.reorderThreshold = reorderThreshold;
            this.dailyConsumption = dailyConsumption;
            this.expiryDate = expiryDate;
        }

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

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private String category;
            private StationName station;
            private Integer quantity;
            private String unit;
            private Integer reorderThreshold;
            private Double dailyConsumption;
            private LocalDate expiryDate;

            public Builder name(String name) { this.name = name; return this; }
            public Builder category(String category) { this.category = category; return this; }
            public Builder station(StationName station) { this.station = station; return this; }
            public Builder quantity(Integer quantity) { this.quantity = quantity; return this; }
            public Builder unit(String unit) { this.unit = unit; return this; }
            public Builder reorderThreshold(Integer reorderThreshold) { this.reorderThreshold = reorderThreshold; return this; }
            public Builder dailyConsumption(Double dailyConsumption) { this.dailyConsumption = dailyConsumption; return this; }
            public Builder expiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; return this; }

            public CreateInventoryItemRequest build() {
                return new CreateInventoryItemRequest(name, category, station, quantity, unit, reorderThreshold, dailyConsumption, expiryDate);
            }
        }
    }

    public static class AdjustInventoryRequest {
        @NotNull(message = "Quantity change is required")
        private Integer changeQuantity;

        private String reason;

        public AdjustInventoryRequest() {}
        public AdjustInventoryRequest(Integer changeQuantity, String reason) {
            this.changeQuantity = changeQuantity;
            this.reason = reason;
        }

        public Integer getChangeQuantity() { return changeQuantity; }
        public void setChangeQuantity(Integer changeQuantity) { this.changeQuantity = changeQuantity; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private Integer changeQuantity;
            private String reason;

            public Builder changeQuantity(Integer changeQuantity) { this.changeQuantity = changeQuantity; return this; }
            public Builder reason(String reason) { this.reason = reason; return this; }

            public AdjustInventoryRequest build() {
                return new AdjustInventoryRequest(changeQuantity, reason);
            }
        }
    }

    public static class InventoryItemDto {
        private UUID id;
        private String name;
        private String category;
        private StationName station;
        private Integer quantity;
        private String unit;
        private Integer reorderThreshold;
        private Double dailyConsumption;
        private LocalDate expiryDate;
        private LocalDateTime lastUpdated;
        private boolean lowStock;
        private boolean criticalStock;
        private Integer estimatedDaysRemaining;

        public InventoryItemDto() {}
        public InventoryItemDto(UUID id, String name, String category, StationName station, Integer quantity,
                                String unit, Integer reorderThreshold, Double dailyConsumption, LocalDate expiryDate,
                                LocalDateTime lastUpdated, boolean lowStock, boolean criticalStock, Integer estimatedDaysRemaining) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.station = station;
            this.quantity = quantity;
            this.unit = unit;
            this.reorderThreshold = reorderThreshold;
            this.dailyConsumption = dailyConsumption;
            this.expiryDate = expiryDate;
            this.lastUpdated = lastUpdated;
            this.lowStock = lowStock;
            this.criticalStock = criticalStock;
            this.estimatedDaysRemaining = estimatedDaysRemaining;
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
        public boolean isLowStock() { return lowStock; }
        public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }
        public boolean isCriticalStock() { return criticalStock; }
        public void setCriticalStock(boolean criticalStock) { this.criticalStock = criticalStock; }
        public Integer getEstimatedDaysRemaining() { return estimatedDaysRemaining; }
        public void setEstimatedDaysRemaining(Integer estimatedDaysRemaining) { this.estimatedDaysRemaining = estimatedDaysRemaining; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String name;
            private String category;
            private StationName station;
            private Integer quantity;
            private String unit;
            private Integer reorderThreshold;
            private Double dailyConsumption;
            private LocalDate expiryDate;
            private LocalDateTime lastUpdated;
            private boolean lowStock;
            private boolean criticalStock;
            private Integer estimatedDaysRemaining;

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
            public Builder lowStock(boolean lowStock) { this.lowStock = lowStock; return this; }
            public Builder criticalStock(boolean criticalStock) { this.criticalStock = criticalStock; return this; }
            public Builder estimatedDaysRemaining(Integer estimatedDaysRemaining) { this.estimatedDaysRemaining = estimatedDaysRemaining; return this; }

            public InventoryItemDto build() {
                return new InventoryItemDto(id, name, category, station, quantity, unit, reorderThreshold, dailyConsumption, expiryDate, lastUpdated, lowStock, criticalStock, estimatedDaysRemaining);
            }
        }
    }

    public static class InventoryAlertDto {
        private UUID itemId;
        private String itemName;
        private String category;
        private StationName station;
        private Integer currentQuantity;
        private Integer threshold;
        private String alertType;
        private String message;

        public InventoryAlertDto() {}
        public InventoryAlertDto(UUID itemId, String itemName, String category, StationName station,
                                 Integer currentQuantity, Integer threshold, String alertType, String message) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.category = category;
            this.station = station;
            this.currentQuantity = currentQuantity;
            this.threshold = threshold;
            this.alertType = alertType;
            this.message = message;
        }

        public UUID getItemId() { return itemId; }
        public void setItemId(UUID itemId) { this.itemId = itemId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public StationName getStation() { return station; }
        public void setStation(StationName station) { this.station = station; }
        public Integer getCurrentQuantity() { return currentQuantity; }
        public void setCurrentQuantity(Integer currentQuantity) { this.currentQuantity = currentQuantity; }
        public Integer getThreshold() { return threshold; }
        public void setThreshold(Integer threshold) { this.threshold = threshold; }
        public String getAlertType() { return alertType; }
        public void setAlertType(String alertType) { this.alertType = alertType; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID itemId;
            private String itemName;
            private String category;
            private StationName station;
            private Integer currentQuantity;
            private Integer threshold;
            private String alertType;
            private String message;

            public Builder itemId(UUID id) { this.itemId = id; return this; }
            public Builder itemName(String name) { this.itemName = name; return this; }
            public Builder category(String cat) { this.category = cat; return this; }
            public Builder station(StationName st) { this.station = st; return this; }
            public Builder currentQuantity(Integer qty) { this.currentQuantity = qty; return this; }
            public Builder threshold(Integer th) { this.threshold = th; return this; }
            public Builder alertType(String type) { this.alertType = type; return this; }
            public Builder message(String msg) { this.message = msg; return this; }

            public InventoryAlertDto build() {
                return new InventoryAlertDto(itemId, itemName, category, station, currentQuantity, threshold, alertType, message);
            }
        }
    }
}
