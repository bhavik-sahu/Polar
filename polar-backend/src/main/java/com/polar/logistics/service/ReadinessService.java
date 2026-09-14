package com.polar.logistics.service;

import com.polar.logistics.dto.ReadinessDtos;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.PersonReadinessStatus;
import com.polar.logistics.entity.ReadinessRequirement;
import com.polar.logistics.entity.User;
import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.entity.enums.PersonRole;
import com.polar.logistics.entity.enums.ReadinessStatus;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.repository.ExpeditionRepository;
import com.polar.logistics.repository.PersonReadinessStatusRepository;
import com.polar.logistics.repository.PersonRepository;
import com.polar.logistics.repository.ReadinessRequirementRepository;
import com.polar.logistics.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReadinessService {

    private final ExpeditionRepository expeditionRepository;
    private final PersonRepository personRepository;
    private final ReadinessRequirementRepository requirementRepository;
    private final PersonReadinessStatusRepository statusRepository;
    private final UserRepository userRepository;

    public ReadinessService(ExpeditionRepository expeditionRepository,
                            PersonRepository personRepository,
                            ReadinessRequirementRepository requirementRepository,
                            PersonReadinessStatusRepository statusRepository,
                            UserRepository userRepository) {
        this.expeditionRepository = expeditionRepository;
        this.personRepository = personRepository;
        this.requirementRepository = requirementRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void ensureDefaultRequirements() {
        if (requirementRepository.count() == 0) {
            requirementRepository.save(new ReadinessRequirement(null, "Medical Fitness Board Clearance", null, true, "Full polar wintering medical board certification."));
            requirementRepository.save(new ReadinessRequirement(null, "Passport & Polar Clearance Submitted", null, true, "Valid Indian passport with minimum 1-year validity."));
            requirementRepository.save(new ReadinessRequirement(null, "Cold-Weather & Glacier Safety Training", null, true, "Auli snow-craft and crevasse rescue course completed."));
            requirementRepository.save(new ReadinessRequirement(null, "Extreme Cold Polar Gear Issued", null, true, "Grade-V polar kit and satellite beacon assigned."));
            requirementRepository.save(new ReadinessRequirement(null, "Emergency Contact & Consent Executed", null, true, "Next-of-kin emergency notification declaration."));
        }
    }

    @Transactional
    public void autoInitializeCrewReadiness(Person person) {
        ensureDefaultRequirements();
        List<ReadinessRequirement> reqs = requirementRepository.findApplicableRequirements(person.getRole());
        for (ReadinessRequirement r : reqs) {
            if (statusRepository.findByPersonIdAndRequirementId(person.getId(), r.getId()).isEmpty()) {
                // If the person has fitnessClearanceStatus == CLEARED and the requirement is medical, auto-verify
                ReadinessStatus initialStatus = ReadinessStatus.PENDING;
                if (r.getName().toLowerCase().contains("medical") && person.getFitnessClearanceStatus() == FitnessClearanceStatus.CLEARED) {
                    initialStatus = ReadinessStatus.VERIFIED;
                }
                statusRepository.save(new PersonReadinessStatus(null, person, r, initialStatus, LocalDateTime.now(), null, "Auto-initialized on crew assignment"));
            }
        }
    }

    @Transactional
    public ReadinessDtos.ExpeditionReadinessDto getExpeditionReadiness(UUID expeditionId) {
        ensureDefaultRequirements();

        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        List<Person> personnel = personRepository.findByExpeditionId(expeditionId);
        List<ReadinessRequirement> allRequirements = requirementRepository.findAll();

        List<ReadinessDtos.PersonReadinessDto> personDtos = new ArrayList<>();
        int totalReadyCount = 0;
        int totalUnresolvedMandatory = 0;

        for (Person person : personnel) {
            autoInitializeCrewReadiness(person);
            List<PersonReadinessStatus> statuses = statusRepository.findByPersonId(person.getId());

            int totalMandatory = 0;
            int verifiedMandatory = 0;
            List<ReadinessDtos.PersonRequirementStatusDto> reqStatusDtos = new ArrayList<>();

            for (PersonReadinessStatus prs : statuses) {
                ReadinessRequirement req = prs.getRequirement();
                if (req.getIsMandatory() != null && req.getIsMandatory()) {
                    totalMandatory++;
                    if (prs.getStatus() == ReadinessStatus.VERIFIED) {
                        verifiedMandatory++;
                    } else {
                        totalUnresolvedMandatory++;
                    }
                }

                String verifiedByName = prs.getVerifiedBy() != null ? prs.getVerifiedBy().getUsername() : null;
                reqStatusDtos.add(new ReadinessDtos.PersonRequirementStatusDto(
                        prs.getId(),
                        req.getId(),
                        req.getName(),
                        req.getAppliesToRole(),
                        req.getIsMandatory(),
                        prs.getStatus(),
                        prs.getUpdatedAt(),
                        verifiedByName,
                        prs.getNotes()
                ));
            }

            double percent = totalMandatory > 0 ? (double) verifiedMandatory / totalMandatory * 100.0 : 100.0;
            boolean isFullyReady = verifiedMandatory >= totalMandatory && totalMandatory > 0;
            if (isFullyReady) {
                totalReadyCount++;
            }

            personDtos.add(new ReadinessDtos.PersonReadinessDto(
                    person.getId(),
                    person.getName(),
                    person.getRole(),
                    person.getCurrentLocation(),
                    person.getFitnessClearanceStatus(),
                    totalMandatory,
                    verifiedMandatory,
                    Math.round(percent * 10.0) / 10.0,
                    isFullyReady,
                    reqStatusDtos
            ));
        }

        double overallPercent = personnel.size() > 0 ? (double) totalReadyCount / personnel.size() * 100.0 : 100.0;

        long daysUntilDeparture = 0;
        boolean isNearDeparture = false;
        if (expedition.getStartDate() != null) {
            daysUntilDeparture = ChronoUnit.DAYS.between(LocalDate.now(), expedition.getStartDate());
            isNearDeparture = daysUntilDeparture >= 0 && daysUntilDeparture <= 7;
        }

        boolean hasDepartureWarning = isNearDeparture && overallPercent < 100.0;

        List<ReadinessDtos.RequirementDto> reqDtos = allRequirements.stream()
                .map(r -> new ReadinessDtos.RequirementDto(r.getId(), r.getName(), r.getAppliesToRole(), r.getIsMandatory(), r.getDescription()))
                .collect(Collectors.toList());

        return new ReadinessDtos.ExpeditionReadinessDto(
                expedition.getId(),
                expedition.getName(),
                expedition.getStartDate(),
                expedition.getEndDate(),
                expedition.getStatus().name(),
                daysUntilDeparture,
                isNearDeparture,
                hasDepartureWarning,
                personnel.size(),
                totalReadyCount,
                Math.round(overallPercent * 10.0) / 10.0,
                totalUnresolvedMandatory,
                reqDtos,
                personDtos
        );
    }

    @Transactional
    public ReadinessDtos.PersonRequirementStatusDto updateRequirementStatus(UUID statusId,
                                                                           ReadinessDtos.UpdateReadinessStatusRequest request,
                                                                           String username) {
        PersonReadinessStatus status = statusRepository.findById(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("Readiness status entry not found with ID: " + statusId));

        status.setStatus(request.getStatus());
        status.setUpdatedAt(LocalDateTime.now());
        if (request.getNotes() != null) {
            status.setNotes(request.getNotes());
        }

        if (username != null && (request.getStatus() == ReadinessStatus.VERIFIED || request.getStatus() == ReadinessStatus.REJECTED)) {
            userRepository.findByUsername(username).ifPresent(status::setVerifiedBy);
        }

        statusRepository.save(status);

        String verifierName = status.getVerifiedBy() != null ? status.getVerifiedBy().getUsername() : null;
        return new ReadinessDtos.PersonRequirementStatusDto(
                status.getId(),
                status.getRequirement().getId(),
                status.getRequirement().getName(),
                status.getRequirement().getAppliesToRole(),
                status.getRequirement().getIsMandatory(),
                status.getStatus(),
                status.getUpdatedAt(),
                verifierName,
                status.getNotes()
        );
    }

    @Transactional(readOnly = true)
    public List<ReadinessDtos.RequirementDto> getAllRequirements() {
        ensureDefaultRequirements();
        return requirementRepository.findAll().stream()
                .map(r -> new ReadinessDtos.RequirementDto(r.getId(), r.getName(), r.getAppliesToRole(), r.getIsMandatory(), r.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReadinessDtos.RequirementDto createRequirement(ReadinessDtos.CreateRequirementRequest request) {
        ReadinessRequirement req = new ReadinessRequirement(
                null,
                request.getName(),
                request.getAppliesToRole(),
                request.getIsMandatory() != null ? request.getIsMandatory() : true,
                request.getDescription()
        );
        ReadinessRequirement saved = requirementRepository.save(req);
        return new ReadinessDtos.RequirementDto(saved.getId(), saved.getName(), saved.getAppliesToRole(), saved.getIsMandatory(), saved.getDescription());
    }
}
