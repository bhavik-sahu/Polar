package com.polar.logistics.service;

import com.polar.logistics.dto.EmergencyDtos;
import com.polar.logistics.entity.*;
import com.polar.logistics.entity.enums.EmergencySeverity;
import com.polar.logistics.entity.enums.EmergencyStatus;
import com.polar.logistics.entity.enums.EmergencyTriggerType;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.exception.ValidationConflictException;
import com.polar.logistics.repository.*;
import com.polar.logistics.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmergencyResponseService {

    private static final Logger log = LoggerFactory.getLogger(EmergencyResponseService.class);

    private final EmergencyIncidentRepository emergencyIncidentRepository;
    private final EmergencyResponseLogRepository emergencyResponseLogRepository;
    private final PersonRepository personRepository;
    private final ExpeditionRepository expeditionRepository;
    private final UserRepository userRepository;

    public EmergencyResponseService(EmergencyIncidentRepository emergencyIncidentRepository,
                                    EmergencyResponseLogRepository emergencyResponseLogRepository,
                                    PersonRepository personRepository,
                                    ExpeditionRepository expeditionRepository,
                                    UserRepository userRepository) {
        this.emergencyIncidentRepository = emergencyIncidentRepository;
        this.emergencyResponseLogRepository = emergencyResponseLogRepository;
        this.personRepository = personRepository;
        this.expeditionRepository = expeditionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EmergencyDtos.EmergencyIncidentDto triggerManualSos(EmergencyDtos.ManualSosRequest request, UserPrincipal currentUser) {
        Person person = null;
        if (request.getPersonId() != null) {
            person = personRepository.findById(request.getPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + request.getPersonId()));

            if (emergencyIncidentRepository.existsActiveIncidentForPerson(person.getId())) {
                throw new ValidationConflictException("An active emergency incident is already registered for " + person.getName());
            }
        }

        Expedition expedition = null;
        if (request.getExpeditionId() != null) {
            expedition = expeditionRepository.findById(request.getExpeditionId()).orElse(null);
        } else if (person != null && person.getExpedition() != null) {
            expedition = person.getExpedition();
        }

        EmergencyIncident incident = EmergencyIncident.builder()
                .triggerType(EmergencyTriggerType.MANUAL_SOS)
                .triggeredBy(person)
                .relatedExpedition(expedition)
                .station(request.getStation())
                .severity(request.getSeverity() != null ? request.getSeverity() : EmergencySeverity.HIGH)
                .status(EmergencyStatus.ACTIVE)
                .lastKnownLatitude(request.getLatitude() != null ? request.getLatitude() : (person != null ? person.getLastKnownLatitude() : null))
                .lastKnownLongitude(request.getLongitude() != null ? request.getLongitude() : (person != null ? person.getLastKnownLongitude() : null))
                .triggeredAt(LocalDateTime.now())
                .resolutionNotes(request.getNote())
                .responseLogs(new ArrayList<>())
                .build();

        EmergencyIncident saved = emergencyIncidentRepository.save(incident);
        log.warn("MANUAL SOS TRIGGERED: Incident ID: {} by Person: {}", saved.getId(), person != null ? person.getName() : "Anonymous");

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EmergencyDtos.EmergencyIncidentDto> getActiveEmergencies() {
        return emergencyIncidentRepository.findByStatusOrderByTriggeredAtDesc(EmergencyStatus.ACTIVE)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmergencyDtos.EmergencyIncidentDto> getAllEmergencies() {
        return emergencyIncidentRepository.findAllByOrderByTriggeredAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmergencyDtos.EmergencyIncidentDto getEmergencyById(UUID id) {
        EmergencyIncident incident = emergencyIncidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency incident not found with ID: " + id));
        return mapToDto(incident);
    }

    @Transactional
    public EmergencyDtos.EmergencyIncidentDto resolveEmergency(UUID id, EmergencyDtos.ResolveEmergencyRequest request, UserPrincipal currentUser) {
        EmergencyIncident incident = emergencyIncidentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency incident not found with ID: " + id));

        incident.setStatus(EmergencyStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
        incident.setResolutionNotes(request.getResolutionNotes());

        if (currentUser != null) {
            User resolver = userRepository.findById(currentUser.getId()).orElse(null);
            if (resolver != null) {
                EmergencyResponseLog resolveLog = EmergencyResponseLog.builder()
                        .incident(incident)
                        .actionTaken("Emergency resolved: " + request.getResolutionNotes())
                        .performedBy(resolver)
                        .timestamp(LocalDateTime.now())
                        .build();
                emergencyResponseLogRepository.save(resolveLog);
            }
        }

        return mapToDto(emergencyIncidentRepository.save(incident));
    }

    @Transactional
    public EmergencyDtos.EmergencyResponseLogDto addResponseLog(UUID incidentId, EmergencyDtos.EmergencyResponseLogRequest request, UserPrincipal currentUser) {
        EmergencyIncident incident = emergencyIncidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Emergency incident not found with ID: " + incidentId));

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + currentUser.getId()));

        EmergencyResponseLog logEntry = EmergencyResponseLog.builder()
                .incident(incident)
                .actionTaken(request.getActionTaken())
                .performedBy(user)
                .timestamp(LocalDateTime.now())
                .build();

        EmergencyResponseLog saved = emergencyResponseLogRepository.save(logEntry);

        return EmergencyDtos.EmergencyResponseLogDto.builder()
                .id(saved.getId())
                .actionTaken(saved.getActionTaken())
                .performedById(user.getId())
                .performedByName(user.getUsername())
                .timestamp(saved.getTimestamp())
                .build();
    }

    private EmergencyDtos.EmergencyIncidentDto mapToDto(EmergencyIncident incident) {
        List<EmergencyResponseLog> logs = emergencyResponseLogRepository.findByIncidentIdOrderByTimestampDesc(incident.getId());

        return EmergencyDtos.EmergencyIncidentDto.builder()
                .id(incident.getId())
                .triggerType(incident.getTriggerType())
                .triggeredByPersonId(incident.getTriggeredBy() != null ? incident.getTriggeredBy().getId() : null)
                .triggeredByPersonName(incident.getTriggeredBy() != null ? incident.getTriggeredBy().getName() : null)
                .relatedExpeditionId(incident.getRelatedExpedition() != null ? incident.getRelatedExpedition().getId() : null)
                .relatedExpeditionName(incident.getRelatedExpedition() != null ? incident.getRelatedExpedition().getName() : null)
                .station(incident.getStation())
                .severity(incident.getSeverity())
                .status(incident.getStatus())
                .lastKnownLatitude(incident.getLastKnownLatitude())
                .lastKnownLongitude(incident.getLastKnownLongitude())
                .triggeredAt(incident.getTriggeredAt())
                .resolvedAt(incident.getResolvedAt())
                .resolutionNotes(incident.getResolutionNotes())
                .responseLogs(logs.stream().map(l -> EmergencyDtos.EmergencyResponseLogDto.builder()
                        .id(l.getId())
                        .actionTaken(l.getActionTaken())
                        .performedById(l.getPerformedBy() != null ? l.getPerformedBy().getId() : null)
                        .performedByName(l.getPerformedBy() != null ? l.getPerformedBy().getUsername() : null)
                        .timestamp(l.getTimestamp())
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }
}
