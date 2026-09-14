package com.polar.logistics.document;

import com.polar.logistics.entity.enums.PingEntityType;
import com.polar.logistics.entity.enums.PingSource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "status_pings")
@CompoundIndex(name = "entity_time_idx", def = "{'entityType': 1, 'entityId': 1, 'timestamp': -1}")
public class StatusPing {

    @Id
    private String id;

    @Indexed
    private PingEntityType entityType;

    @Indexed
    private UUID entityId;

    @Indexed
    private LocalDateTime timestamp;

    private Double latitude;

    private Double longitude;

    private String statusNote;

    private PingSource source;

    public StatusPing() {}

    public StatusPing(String id, PingEntityType entityType, UUID entityId, LocalDateTime timestamp,
                      Double latitude, Double longitude, String statusNote, PingSource source) {
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

        public StatusPing build() {
            return new StatusPing(id, entityType, entityId, timestamp, latitude, longitude, statusNote, source);
        }
    }
}
