package com.polar.logistics.entity;

import com.polar.logistics.entity.enums.ReadinessStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "person_readiness_statuses")
public class PersonReadinessStatus {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requirement_id", nullable = false)
    private ReadinessRequirement requirement;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReadinessStatus status = ReadinessStatus.PENDING;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id")
    private User verifiedBy;

    @Column(name = "notes")
    private String notes;

    public PersonReadinessStatus() {}

    public PersonReadinessStatus(UUID id, Person person, ReadinessRequirement requirement,
                                 ReadinessStatus status, LocalDateTime updatedAt, User verifiedBy, String notes) {
        this.id = id;
        this.person = person;
        this.requirement = requirement;
        this.status = status != null ? status : ReadinessStatus.PENDING;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.verifiedBy = verifiedBy;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public ReadinessRequirement getRequirement() { return requirement; }
    public void setRequirement(ReadinessRequirement requirement) { this.requirement = requirement; }

    public ReadinessStatus getStatus() { return status; }
    public void setStatus(ReadinessStatus status) { this.status = status; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private Person person;
        private ReadinessRequirement requirement;
        private ReadinessStatus status = ReadinessStatus.PENDING;
        private LocalDateTime updatedAt = LocalDateTime.now();
        private User verifiedBy;
        private String notes;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder person(Person person) { this.person = person; return this; }
        public Builder requirement(ReadinessRequirement requirement) { this.requirement = requirement; return this; }
        public Builder status(ReadinessStatus status) { this.status = status; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder verifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public PersonReadinessStatus build() {
            return new PersonReadinessStatus(id, person, requirement, status, updatedAt, verifiedBy, notes);
        }
    }
}
