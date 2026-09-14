package com.polar.logistics.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.polar.logistics.entity.enums.ExpeditionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expeditions")
public class Expedition {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "objective", columnDefinition = "TEXT")
    private String objective;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExpeditionStatus status = ExpeditionStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "expedition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceOrder ASC")
    @JsonManagedReference
    private List<TransitLeg> transitLegs = new ArrayList<>();

    public Expedition() {}

    public Expedition(UUID id, String name, String objective, LocalDate startDate, LocalDate endDate,
                      ExpeditionStatus status, User createdBy, List<TransitLeg> transitLegs) {
        this.id = id;
        this.name = name;
        this.objective = objective;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : ExpeditionStatus.PLANNED;
        this.createdBy = createdBy;
        this.transitLegs = transitLegs != null ? transitLegs : new ArrayList<>();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public ExpeditionStatus getStatus() { return status; }
    public void setStatus(ExpeditionStatus status) { this.status = status; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public List<TransitLeg> getTransitLegs() { return transitLegs; }
    public void setTransitLegs(List<TransitLeg> transitLegs) { this.transitLegs = transitLegs; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String name;
        private String objective;
        private LocalDate startDate;
        private LocalDate endDate;
        private ExpeditionStatus status = ExpeditionStatus.PLANNED;
        private User createdBy;
        private List<TransitLeg> transitLegs = new ArrayList<>();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder objective(String objective) { this.objective = objective; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder status(ExpeditionStatus status) { this.status = status; return this; }
        public Builder createdBy(User createdBy) { this.createdBy = createdBy; return this; }
        public Builder transitLegs(List<TransitLeg> transitLegs) { this.transitLegs = transitLegs; return this; }

        public Expedition build() {
            return new Expedition(id, name, objective, startDate, endDate, status, createdBy, transitLegs);
        }
    }
}
