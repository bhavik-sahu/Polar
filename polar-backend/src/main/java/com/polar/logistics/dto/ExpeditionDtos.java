package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.ExpeditionStatus;
import com.polar.logistics.entity.enums.TransitMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ExpeditionDtos {

    public static class CreateExpeditionRequest {
        @NotBlank(message = "Expedition name is required")
        private String name;

        private String objective;

        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        private LocalDate endDate;

        @NotEmpty(message = "At least one transit leg is required")
        @Valid
        private List<TransitLegRequest> transitLegs;

        public CreateExpeditionRequest() {}
        public CreateExpeditionRequest(String name, String objective, LocalDate startDate, LocalDate endDate, List<TransitLegRequest> transitLegs) {
            this.name = name;
            this.objective = objective;
            this.startDate = startDate;
            this.endDate = endDate;
            this.transitLegs = transitLegs;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getObjective() { return objective; }
        public void setObjective(String objective) { this.objective = objective; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public List<TransitLegRequest> getTransitLegs() { return transitLegs; }
        public void setTransitLegs(List<TransitLegRequest> transitLegs) { this.transitLegs = transitLegs; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private String name;
            private String objective;
            private LocalDate startDate;
            private LocalDate endDate;
            private List<TransitLegRequest> transitLegs;

            public Builder name(String name) { this.name = name; return this; }
            public Builder objective(String objective) { this.objective = objective; return this; }
            public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
            public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
            public Builder transitLegs(List<TransitLegRequest> transitLegs) { this.transitLegs = transitLegs; return this; }

            public CreateExpeditionRequest build() {
                return new CreateExpeditionRequest(name, objective, startDate, endDate, transitLegs);
            }
        }
    }

    public static class TransitLegRequest {
        private UUID id;

        @NotNull(message = "Sequence order is required")
        private Integer sequenceOrder;

        @NotBlank(message = "Origin is required")
        private String origin;

        @NotBlank(message = "Destination is required")
        private String destination;

        @NotNull(message = "Mode is required")
        private TransitMode mode;

        @NotNull(message = "Expected departure is required")
        private LocalDateTime expectedDeparture;

        @NotNull(message = "Expected arrival is required")
        private LocalDateTime expectedArrival;

        private LocalDateTime actualDeparture;
        private LocalDateTime actualArrival;

        public TransitLegRequest() {}
        public TransitLegRequest(UUID id, Integer sequenceOrder, String origin, String destination, TransitMode mode,
                                 LocalDateTime expectedDeparture, LocalDateTime expectedArrival,
                                 LocalDateTime actualDeparture, LocalDateTime actualArrival) {
            this.id = id;
            this.sequenceOrder = sequenceOrder;
            this.origin = origin;
            this.destination = destination;
            this.mode = mode;
            this.expectedDeparture = expectedDeparture;
            this.expectedArrival = expectedArrival;
            this.actualDeparture = actualDeparture;
            this.actualArrival = actualArrival;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
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

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private Integer sequenceOrder;
            private String origin;
            private String destination;
            private TransitMode mode;
            private LocalDateTime expectedDeparture;
            private LocalDateTime expectedArrival;
            private LocalDateTime actualDeparture;
            private LocalDateTime actualArrival;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder sequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; return this; }
            public Builder origin(String origin) { this.origin = origin; return this; }
            public Builder destination(String destination) { this.destination = destination; return this; }
            public Builder mode(TransitMode mode) { this.mode = mode; return this; }
            public Builder expectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; return this; }
            public Builder expectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; return this; }
            public Builder actualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; return this; }
            public Builder actualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; return this; }

            public TransitLegRequest build() {
                return new TransitLegRequest(id, sequenceOrder, origin, destination, mode, expectedDeparture, expectedArrival, actualDeparture, actualArrival);
            }
        }
    }

    public static class TransitLegDto {
        private UUID id;
        private UUID expeditionId;
        private Integer sequenceOrder;
        private String origin;
        private String destination;
        private TransitMode mode;
        private LocalDateTime expectedDeparture;
        private LocalDateTime expectedArrival;
        private LocalDateTime actualDeparture;
        private LocalDateTime actualArrival;

        public TransitLegDto() {}
        public TransitLegDto(UUID id, UUID expeditionId, Integer sequenceOrder, String origin, String destination,
                             TransitMode mode, LocalDateTime expectedDeparture, LocalDateTime expectedArrival,
                             LocalDateTime actualDeparture, LocalDateTime actualArrival) {
            this.id = id;
            this.expeditionId = expeditionId;
            this.sequenceOrder = sequenceOrder;
            this.origin = origin;
            this.destination = destination;
            this.mode = mode;
            this.expectedDeparture = expectedDeparture;
            this.expectedArrival = expectedArrival;
            this.actualDeparture = actualDeparture;
            this.actualArrival = actualArrival;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }
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

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private UUID expeditionId;
            private Integer sequenceOrder;
            private String origin;
            private String destination;
            private TransitMode mode;
            private LocalDateTime expectedDeparture;
            private LocalDateTime expectedArrival;
            private LocalDateTime actualDeparture;
            private LocalDateTime actualArrival;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder expeditionId(UUID expeditionId) { this.expeditionId = expeditionId; return this; }
            public Builder sequenceOrder(Integer sequenceOrder) { this.sequenceOrder = sequenceOrder; return this; }
            public Builder origin(String origin) { this.origin = origin; return this; }
            public Builder destination(String destination) { this.destination = destination; return this; }
            public Builder mode(TransitMode mode) { this.mode = mode; return this; }
            public Builder expectedDeparture(LocalDateTime expectedDeparture) { this.expectedDeparture = expectedDeparture; return this; }
            public Builder expectedArrival(LocalDateTime expectedArrival) { this.expectedArrival = expectedArrival; return this; }
            public Builder actualDeparture(LocalDateTime actualDeparture) { this.actualDeparture = actualDeparture; return this; }
            public Builder actualArrival(LocalDateTime actualArrival) { this.actualArrival = actualArrival; return this; }

            public TransitLegDto build() {
                return new TransitLegDto(id, expeditionId, sequenceOrder, origin, destination, mode, expectedDeparture, expectedArrival, actualDeparture, actualArrival);
            }
        }
    }

    public static class ExpeditionDto {
        private UUID id;
        private String name;
        private String objective;
        private LocalDate startDate;
        private LocalDate endDate;
        private ExpeditionStatus status;
        private UUID createdById;
        private String createdByName;
        private List<TransitLegDto> transitLegs;
        private List<PersonnelDtos.PersonDto> personnel;
        private List<CargoDtos.CargoItemDto> cargo;

        public ExpeditionDto() {}
        public ExpeditionDto(UUID id, String name, String objective, LocalDate startDate, LocalDate endDate,
                             ExpeditionStatus status, UUID createdById, String createdByName,
                             List<TransitLegDto> transitLegs, List<PersonnelDtos.PersonDto> personnel,
                             List<CargoDtos.CargoItemDto> cargo) {
            this.id = id;
            this.name = name;
            this.objective = objective;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.createdById = createdById;
            this.createdByName = createdByName;
            this.transitLegs = transitLegs;
            this.personnel = personnel;
            this.cargo = cargo;
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
        public UUID getCreatedById() { return createdById; }
        public void setCreatedById(UUID createdById) { this.createdById = createdById; }
        public String getCreatedByName() { return createdByName; }
        public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
        public List<TransitLegDto> getTransitLegs() { return transitLegs; }
        public void setTransitLegs(List<TransitLegDto> transitLegs) { this.transitLegs = transitLegs; }
        public List<PersonnelDtos.PersonDto> getPersonnel() { return personnel; }
        public void setPersonnel(List<PersonnelDtos.PersonDto> personnel) { this.personnel = personnel; }
        public List<CargoDtos.CargoItemDto> getCargo() { return cargo; }
        public void setCargo(List<CargoDtos.CargoItemDto> cargo) { this.cargo = cargo; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String name;
            private String objective;
            private LocalDate startDate;
            private LocalDate endDate;
            private ExpeditionStatus status;
            private UUID createdById;
            private String createdByName;
            private List<TransitLegDto> transitLegs;
            private List<PersonnelDtos.PersonDto> personnel;
            private List<CargoDtos.CargoItemDto> cargo;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder objective(String objective) { this.objective = objective; return this; }
            public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
            public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
            public Builder status(ExpeditionStatus status) { this.status = status; return this; }
            public Builder createdById(UUID createdById) { this.createdById = createdById; return this; }
            public Builder createdByName(String createdByName) { this.createdByName = createdByName; return this; }
            public Builder transitLegs(List<TransitLegDto> transitLegs) { this.transitLegs = transitLegs; return this; }
            public Builder personnel(List<PersonnelDtos.PersonDto> personnel) { this.personnel = personnel; return this; }
            public Builder cargo(List<CargoDtos.CargoItemDto> cargo) { this.cargo = cargo; return this; }

            public ExpeditionDto build() {
                return new ExpeditionDto(id, name, objective, startDate, endDate, status, createdById, createdByName, transitLegs, personnel, cargo);
            }
        }
    }

    public static class ExpeditionSummaryDto {
        private UUID id;
        private String name;
        private String objective;
        private LocalDate startDate;
        private LocalDate endDate;
        private ExpeditionStatus status;
        private int totalLegs;
        private int totalPersonnel;
        private int totalCargoItems;
        private double totalCargoWeightKg;

        public ExpeditionSummaryDto() {}
        public ExpeditionSummaryDto(UUID id, String name, String objective, LocalDate startDate, LocalDate endDate,
                                    ExpeditionStatus status, int totalLegs, int totalPersonnel,
                                    int totalCargoItems, double totalCargoWeightKg) {
            this.id = id;
            this.name = name;
            this.objective = objective;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.totalLegs = totalLegs;
            this.totalPersonnel = totalPersonnel;
            this.totalCargoItems = totalCargoItems;
            this.totalCargoWeightKg = totalCargoWeightKg;
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
        public int getTotalLegs() { return totalLegs; }
        public void setTotalLegs(int totalLegs) { this.totalLegs = totalLegs; }
        public int getTotalPersonnel() { return totalPersonnel; }
        public void setTotalPersonnel(int totalPersonnel) { this.totalPersonnel = totalPersonnel; }
        public int getTotalCargoItems() { return totalCargoItems; }
        public void setTotalCargoItems(int totalCargoItems) { this.totalCargoItems = totalCargoItems; }
        public double getTotalCargoWeightKg() { return totalCargoWeightKg; }
        public void setTotalCargoWeightKg(double totalCargoWeightKg) { this.totalCargoWeightKg = totalCargoWeightKg; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private UUID id;
            private String name;
            private String objective;
            private LocalDate startDate;
            private LocalDate endDate;
            private ExpeditionStatus status;
            private int totalLegs;
            private int totalPersonnel;
            private int totalCargoItems;
            private double totalCargoWeightKg;

            public Builder id(UUID id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder objective(String objective) { this.objective = objective; return this; }
            public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
            public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
            public Builder status(ExpeditionStatus status) { this.status = status; return this; }
            public Builder totalLegs(int totalLegs) { this.totalLegs = totalLegs; return this; }
            public Builder totalPersonnel(int totalPersonnel) { this.totalPersonnel = totalPersonnel; return this; }
            public Builder totalCargoItems(int totalCargoItems) { this.totalCargoItems = totalCargoItems; return this; }
            public Builder totalCargoWeightKg(double totalCargoWeightKg) { this.totalCargoWeightKg = totalCargoWeightKg; return this; }

            public ExpeditionSummaryDto build() {
                return new ExpeditionSummaryDto(id, name, objective, startDate, endDate, status, totalLegs, totalPersonnel, totalCargoItems, totalCargoWeightKg);
            }
        }
    }

    public static class UpdateExpeditionStatusRequest {
        @NotNull(message = "Status is required")
        private ExpeditionStatus status;

        public UpdateExpeditionStatusRequest() {}
        public UpdateExpeditionStatusRequest(ExpeditionStatus status) { this.status = status; }

        public ExpeditionStatus getStatus() { return status; }
        public void setStatus(ExpeditionStatus status) { this.status = status; }
    }

    public static class UpdateTransitLegsRequest {
        @NotEmpty(message = "Transit legs list cannot be empty")
        @Valid
        private List<TransitLegRequest> transitLegs;

        public UpdateTransitLegsRequest() {}
        public UpdateTransitLegsRequest(List<TransitLegRequest> transitLegs) { this.transitLegs = transitLegs; }

        public List<TransitLegRequest> getTransitLegs() { return transitLegs; }
        public void setTransitLegs(List<TransitLegRequest> transitLegs) { this.transitLegs = transitLegs; }
    }
}
