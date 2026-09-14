package com.polar.logistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.entity.enums.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "station")
    private StationName station;

    public User() {}

    public User(UUID id, String username, String passwordHash, UserRole role, StationName station) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.station = station;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public StationName getStation() { return station; }
    public void setStation(StationName station) { this.station = station; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String username;
        private String passwordHash;
        private UserRole role;
        private StationName station;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder role(UserRole role) { this.role = role; return this; }
        public Builder station(StationName station) { this.station = station; return this; }

        public User build() {
            return new User(id, username, passwordHash, role, station);
        }
    }
}
