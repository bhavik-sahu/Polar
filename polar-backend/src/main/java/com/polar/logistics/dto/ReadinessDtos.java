package com.polar.logistics.dto;

import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.entity.enums.PersonRole;
import com.polar.logistics.entity.enums.ReadinessStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class ReadinessDtos {

    private ReadinessDtos() {}

    public static class RequirementDto {
        private UUID id;
        private String name;
        private PersonRole appliesToRole;
        private Boolean isMandatory;
        private String description;

        public RequirementDto() {}

        public RequirementDto(UUID id, String name, PersonRole appliesToRole, Boolean isMandatory, String description) {
            this.id = id;
            this.name = name;
            this.appliesToRole = appliesToRole;
            this.isMandatory = isMandatory;
            this.description = description;
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public PersonRole getAppliesToRole() { return appliesToRole; }
        public void setAppliesToRole(PersonRole appliesToRole) { this.appliesToRole = appliesToRole; }

        public Boolean getIsMandatory() { return isMandatory; }
        public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class PersonRequirementStatusDto {
        private UUID statusId;
        private UUID requirementId;
        private String requirementName;
        private PersonRole appliesToRole;
        private Boolean isMandatory;
        private ReadinessStatus status;
        private LocalDateTime updatedAt;
        private String verifiedByName;
        private String notes;

        public PersonRequirementStatusDto() {}

        public PersonRequirementStatusDto(UUID statusId, UUID requirementId, String requirementName,
                                          PersonRole appliesToRole, Boolean isMandatory, ReadinessStatus status,
                                          LocalDateTime updatedAt, String verifiedByName, String notes) {
            this.statusId = statusId;
            this.requirementId = requirementId;
            this.requirementName = requirementName;
            this.appliesToRole = appliesToRole;
            this.isMandatory = isMandatory;
            this.status = status;
            this.updatedAt = updatedAt;
            this.verifiedByName = verifiedByName;
            this.notes = notes;
        }

        public UUID getStatusId() { return statusId; }
        public void setStatusId(UUID statusId) { this.statusId = statusId; }

        public UUID getRequirementId() { return requirementId; }
        public void setRequirementId(UUID requirementId) { this.requirementId = requirementId; }

        public String getRequirementName() { return requirementName; }
        public void setRequirementName(String requirementName) { this.requirementName = requirementName; }

        public PersonRole getAppliesToRole() { return appliesToRole; }
        public void setAppliesToRole(PersonRole appliesToRole) { this.appliesToRole = appliesToRole; }

        public Boolean getIsMandatory() { return isMandatory; }
        public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

        public ReadinessStatus getStatus() { return status; }
        public void setStatus(ReadinessStatus status) { this.status = status; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

        public String getVerifiedByName() { return verifiedByName; }
        public void setVerifiedByName(String verifiedByName) { this.verifiedByName = verifiedByName; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class PersonReadinessDto {
        private UUID personId;
        private String personName;
        private PersonRole role;
        private String station;
        private FitnessClearanceStatus fitnessClearanceStatus;
        private int totalMandatory;
        private int verifiedMandatory;
        private double readinessPercent;
        private boolean isFullyReady;
        private List<PersonRequirementStatusDto> requirements;

        public PersonReadinessDto() {}

        public PersonReadinessDto(UUID personId, String personName, PersonRole role, String station,
                                  FitnessClearanceStatus fitnessClearanceStatus, int totalMandatory,
                                  int verifiedMandatory, double readinessPercent, boolean isFullyReady,
                                  List<PersonRequirementStatusDto> requirements) {
            this.personId = personId;
            this.personName = personName;
            this.role = role;
            this.station = station;
            this.fitnessClearanceStatus = fitnessClearanceStatus;
            this.totalMandatory = totalMandatory;
            this.verifiedMandatory = verifiedMandatory;
            this.readinessPercent = readinessPercent;
            this.isFullyReady = isFullyReady;
            this.requirements = requirements;
        }

        public UUID getPersonId() { return personId; }
        public void setPersonId(UUID personId) { this.personId = personId; }

        public String getPersonName() { return personName; }
        public void setPersonName(String personName) { this.personName = personName; }

        public PersonRole getRole() { return role; }
        public void setRole(PersonRole role) { this.role = role; }

        public String getStation() { return station; }
        public void setStation(String station) { this.station = station; }

        public FitnessClearanceStatus getFitnessClearanceStatus() { return fitnessClearanceStatus; }
        public void setFitnessClearanceStatus(FitnessClearanceStatus fitnessClearanceStatus) { this.fitnessClearanceStatus = fitnessClearanceStatus; }

        public int getTotalMandatory() { return totalMandatory; }
        public void setTotalMandatory(int totalMandatory) { this.totalMandatory = totalMandatory; }

        public int getVerifiedMandatory() { return verifiedMandatory; }
        public void setVerifiedMandatory(int verifiedMandatory) { this.verifiedMandatory = verifiedMandatory; }

        public double getReadinessPercent() { return readinessPercent; }
        public void setReadinessPercent(double readinessPercent) { this.readinessPercent = readinessPercent; }

        public boolean isFullyReady() { return isFullyReady; }
        public void setFullyReady(boolean fullyReady) { isFullyReady = fullyReady; }

        public List<PersonRequirementStatusDto> getRequirements() { return requirements; }
        public void setRequirements(List<PersonRequirementStatusDto> requirements) { this.requirements = requirements; }
    }

    public static class ExpeditionReadinessDto {
        private UUID expeditionId;
        private String expeditionName;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
        private long daysUntilDeparture;
        private boolean isNearDeparture; // within 7 days
        private boolean hasDepartureWarning; // near departure and not 100% ready
        private int totalPersons;
        private int totalReady;
        private double overallReadinessPercent;
        private int mandatoryUnresolvedCount;
        private List<RequirementDto> requirements;
        private List<PersonReadinessDto> personnel;

        public ExpeditionReadinessDto() {}

        public ExpeditionReadinessDto(UUID expeditionId, String expeditionName, LocalDate startDate, LocalDate endDate,
                                      String status, long daysUntilDeparture, boolean isNearDeparture,
                                      boolean hasDepartureWarning, int totalPersons, int totalReady,
                                      double overallReadinessPercent, int mandatoryUnresolvedCount,
                                      List<RequirementDto> requirements, List<PersonReadinessDto> personnel) {
            this.expeditionId = expeditionId;
            this.expeditionName = expeditionName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.daysUntilDeparture = daysUntilDeparture;
            this.isNearDeparture = isNearDeparture;
            this.hasDepartureWarning = hasDepartureWarning;
            this.totalPersons = totalPersons;
            this.totalReady = totalReady;
            this.overallReadinessPercent = overallReadinessPercent;
            this.mandatoryUnresolvedCount = mandatoryUnresolvedCount;
            this.requirements = requirements;
            this.personnel = personnel;
        }

        public UUID getExpeditionId() { return expeditionId; }
        public void setExpeditionId(UUID expeditionId) { this.expeditionId = expeditionId; }

        public String getExpeditionName() { return expeditionName; }
        public void setExpeditionName(String expeditionName) { this.expeditionName = expeditionName; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getDaysUntilDeparture() { return daysUntilDeparture; }
        public void setDaysUntilDeparture(long daysUntilDeparture) { this.daysUntilDeparture = daysUntilDeparture; }

        public boolean isNearDeparture() { return isNearDeparture; }
        public void setNearDeparture(boolean nearDeparture) { isNearDeparture = nearDeparture; }

        public boolean isHasDepartureWarning() { return hasDepartureWarning; }
        public void setHasDepartureWarning(boolean hasDepartureWarning) { this.hasDepartureWarning = hasDepartureWarning; }

        public int getTotalPersons() { return totalPersons; }
        public void setTotalPersons(int totalPersons) { this.totalPersons = totalPersons; }

        public int getTotalReady() { return totalReady; }
        public void setTotalReady(int totalReady) { this.totalReady = totalReady; }

        public double getOverallReadinessPercent() { return overallReadinessPercent; }
        public void setOverallReadinessPercent(double overallReadinessPercent) { this.overallReadinessPercent = overallReadinessPercent; }

        public int getMandatoryUnresolvedCount() { return mandatoryUnresolvedCount; }
        public void setMandatoryUnresolvedCount(int mandatoryUnresolvedCount) { this.mandatoryUnresolvedCount = mandatoryUnresolvedCount; }

        public List<RequirementDto> getRequirements() { return requirements; }
        public void setRequirements(List<RequirementDto> requirements) { this.requirements = requirements; }

        public List<PersonReadinessDto> getPersonnel() { return personnel; }
        public void setPersonnel(List<PersonReadinessDto> personnel) { this.personnel = personnel; }
    }

    public static class UpdateReadinessStatusRequest {
        @NotNull(message = "Status is required")
        private ReadinessStatus status;

        private String notes;

        public UpdateReadinessStatusRequest() {}

        public UpdateReadinessStatusRequest(ReadinessStatus status, String notes) {
            this.status = status;
            this.notes = notes;
        }

        public ReadinessStatus getStatus() { return status; }
        public void setStatus(ReadinessStatus status) { this.status = status; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class CreateRequirementRequest {
        @NotBlank(message = "Requirement name is required")
        private String name;

        private PersonRole appliesToRole;

        private Boolean isMandatory = true;

        private String description;

        public CreateRequirementRequest() {}

        public CreateRequirementRequest(String name, PersonRole appliesToRole, Boolean isMandatory, String description) {
            this.name = name;
            this.appliesToRole = appliesToRole;
            this.isMandatory = isMandatory;
            this.description = description;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public PersonRole getAppliesToRole() { return appliesToRole; }
        public void setAppliesToRole(PersonRole appliesToRole) { this.appliesToRole = appliesToRole; }

        public Boolean getIsMandatory() { return isMandatory; }
        public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
