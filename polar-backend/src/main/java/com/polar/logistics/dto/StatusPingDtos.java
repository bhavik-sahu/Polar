package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.PingEntityType;
import com.polar.logistics.entity.enums.PingSource;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class StatusPingDtos {

    public static class StatusPingRequest {
        @NotNull(message = "Entity type is required")
        private PingEntityType entityType;

        @NotNull(message = "Entity ID is required")
        private UUID entityId;

        private Double latitude;
        private Double longitude;
        private String statusNote;
        private PingSource source;
        private LocalDateTime timestamp;

        public StatusPingRequest() {}
        public StatusPingRequest(PingEntityType entityType, UUID entityId, Double latitude, Double longitude, String statusNote, PingSource source, LocalDateTime timestamp) {
            this.entityType = entityType;
            this.entityId = entityId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.statusNote = statusNote;
            this.source = source;
            this.timestamp = timestamp;
        }

        public PingEntityType getEntityType() { return entityType; }
        public void setEntityType(PingEntityType entityType) { this.entityType = entityType; }
        public UUID getEntityId() { return entityId; }
        public void setEntityId(UUID entityId) { this.entityId = entityId; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getStatusNote() { return statusNote; }
        public void setStatusNote(String statusNote) { this.statusNote = statusNote; }
        public PingSource getSource() { return source; }
        public void setSource(PingSource source) { this.source = source; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private PingEntityType entityType;
            private UUID entityId;
            private Double latitude;
            private Double longitude;
            private String statusNote;
            private PingSource source;
            private LocalDateTime timestamp;

            public Builder entityType(PingEntityType entityType) { this.entityType = entityType; return this; }
            public Builder entityId(UUID entityId) { this.entityId = entityId; return this; }
            public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
            public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
            public Builder statusNote(String statusNote) { this.statusNote = statusNote; return this; }
            public Builder source(PingSource source) { this.source = source; return this; }
            public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

            public StatusPingRequest build() {
                return new StatusPingRequest(entityType, entityId, latitude, longitude, statusNote, source, timestamp);
            }
        }
    }

    public static class StatusPingDto {
        private String id;
        private PingEntityType entityType;
        private UUID entityId;
        private LocalDateTime timestamp;
        private Double latitude;
        private Double longitude;
        private String statusNote;
        private PingSource source;

        public StatusPingDto() {}
        public StatusPingDto(String id, PingEntityType entityType, UUID entityId, LocalDateTime timestamp, Double latitude, Double longitude, String statusNote, PingSource source) {
            this.id = id;
            this.entityType = entityType;
            this.entityId = entityId;
            this.timestamp = timestamp;
            this.latitude = latitude;
            this.longitude = longitude;
            this.statusNote = statusNote;
            this.source = source;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public PingEntityType getEntityType() { return entityType; }
        public void setEntityType(PingEntityType entityType) { this.entityType = entityType; }
        public UUID getEntityId() { return entityId; }
        public void setEntityId(UUID entityId) { this.entityId = entityId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getStatusNote() { return statusNote; }
        public void setStatusNote(String statusNote) { this.statusNote = statusNote; }
        public PingSource getSource() { return source; }
        public void setSource(PingSource source) { this.source = source; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String id;
            private PingEntityType entityType;
            private UUID entityId;
            private LocalDateTime timestamp;
            private Double latitude;
            private Double longitude;
            private String statusNote;
            private PingSource source;

            public Builder id(String id) { this.id = id; return this; }
            public Builder entityType(PingEntityType entityType) { this.entityType = entityType; return this; }
            public Builder entityId(UUID entityId) { this.entityId = entityId; return this; }
            public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
            public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
            public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
            public Builder statusNote(String statusNote) { this.statusNote = statusNote; return this; }
            public Builder source(PingSource source) { this.source = source; return this; }

            public StatusPingDto build() {
                return new StatusPingDto(id, entityType, entityId, timestamp, latitude, longitude, statusNote, source);
            }
        }
    }
}
