package com.polar.logistics.entity;

import com.polar.logistics.entity.enums.PersonRole;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "readiness_requirements")
public class ReadinessRequirement {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to_role")
    private PersonRole appliesToRole;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;

    @Column(name = "description")
    private String description;

    public ReadinessRequirement() {}

    public ReadinessRequirement(UUID id, String name, PersonRole appliesToRole, Boolean isMandatory, String description) {
        this.id = id;
        this.name = name;
        this.appliesToRole = appliesToRole;
        this.isMandatory = isMandatory != null ? isMandatory : true;
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

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String name;
        private PersonRole appliesToRole;
        private Boolean isMandatory = true;
        private String description;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder appliesToRole(PersonRole appliesToRole) { this.appliesToRole = appliesToRole; return this; }
        public Builder isMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; return this; }
        public Builder description(String description) { this.description = description; return this; }

        public ReadinessRequirement build() {
            return new ReadinessRequirement(id, name, appliesToRole, isMandatory, description);
        }
    }
}
