package com.polar.logistics.service;

import com.polar.logistics.dto.CargoDtos;
import com.polar.logistics.dto.ExpeditionDtos;
import com.polar.logistics.dto.PersonnelDtos;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.TransitLeg;
import com.polar.logistics.entity.User;
import com.polar.logistics.entity.enums.ExpeditionStatus;
import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.exception.InvalidStateTransitionException;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.exception.ValidationConflictException;
import com.polar.logistics.repository.*;
import com.polar.logistics.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpeditionPlanningService {

    private static final Logger log = LoggerFactory.getLogger(ExpeditionPlanningService.class);

    private final ExpeditionRepository expeditionRepository;
    private final TransitLegRepository transitLegRepository;
    private final PersonRepository personRepository;
    private final CargoItemRepository cargoItemRepository;
    private final UserRepository userRepository;
    private final PersonReadinessStatusRepository personReadinessStatusRepository;

    public ExpeditionPlanningService(ExpeditionRepository expeditionRepository,
                                     TransitLegRepository transitLegRepository,
                                     PersonRepository personRepository,
                                     CargoItemRepository cargoItemRepository,
                                     UserRepository userRepository,
                                     PersonReadinessStatusRepository personReadinessStatusRepository) {
        this.expeditionRepository = expeditionRepository;
        this.transitLegRepository = transitLegRepository;
        this.personRepository = personRepository;
        this.cargoItemRepository = cargoItemRepository;
        this.userRepository = userRepository;
        this.personReadinessStatusRepository = personReadinessStatusRepository;
    }

    @Transactional
    public ExpeditionDtos.ExpeditionDto createExpedition(ExpeditionDtos.CreateExpeditionRequest request, UserPrincipal currentUser) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ValidationConflictException("Expedition start date cannot be after end date");
        }

        validateSequenceIntegrity(request.getTransitLegs());

        User creator = currentUser != null ? userRepository.findById(currentUser.getId()).orElse(null) : null;

        Expedition expedition = Expedition.builder()
                .name(request.getName())
                .objective(request.getObjective())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(ExpeditionStatus.PLANNED)
                .createdBy(creator)
                .transitLegs(new ArrayList<>())
                .build();

        Expedition savedExpedition = expeditionRepository.save(expedition);

        List<TransitLeg> legs = request.getTransitLegs().stream().map(req -> TransitLeg.builder()
                .expedition(savedExpedition)
                .sequenceOrder(req.getSequenceOrder())
                .origin(req.getOrigin())
                .destination(req.getDestination())
                .mode(req.getMode())
                .expectedDeparture(req.getExpectedDeparture())
                .expectedArrival(req.getExpectedArrival())
                .actualDeparture(req.getActualDeparture())
                .actualArrival(req.getActualArrival())
                .build()
        ).collect(Collectors.toList());

        transitLegRepository.saveAll(legs);
        savedExpedition.setTransitLegs(legs);

        return mapToDto(savedExpedition);
    }

    @Transactional
    public ExpeditionDtos.ExpeditionDto updateTransitLegs(UUID expeditionId, ExpeditionDtos.UpdateTransitLegsRequest request) {
        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        validateSequenceIntegrity(request.getTransitLegs());

        transitLegRepository.deleteByExpeditionId(expeditionId);

        List<TransitLeg> newLegs = request.getTransitLegs().stream().map(req -> TransitLeg.builder()
                .expedition(expedition)
                .sequenceOrder(req.getSequenceOrder())
                .origin(req.getOrigin())
                .destination(req.getDestination())
                .mode(req.getMode())
                .expectedDeparture(req.getExpectedDeparture())
                .expectedArrival(req.getExpectedArrival())
                .actualDeparture(req.getActualDeparture())
                .actualArrival(req.getActualArrival())
                .build()
        ).collect(Collectors.toList());

        List<TransitLeg> savedLegs = transitLegRepository.saveAll(newLegs);
        expedition.setTransitLegs(savedLegs);

        return mapToDto(expedition);
    }

    @Transactional
    public ExpeditionDtos.ExpeditionDto updateStatus(UUID expeditionId, ExpeditionStatus newStatus) {
        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        if (newStatus == ExpeditionStatus.ACTIVE) {
            List<TransitLeg> legs = transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(expeditionId);
            if (legs.isEmpty()) {
                throw new InvalidStateTransitionException("Cannot activate expedition: At least one transit leg must be defined");
            }

            List<Person> assignedPersonnel = personRepository.findByExpeditionId(expeditionId);
            boolean hasClearedMember = assignedPersonnel.stream()
                    .anyMatch(p -> p.getFitnessClearanceStatus() == FitnessClearanceStatus.CLEARED);

            if (!hasClearedMember) {
                throw new InvalidStateTransitionException("Cannot activate expedition: At least one assigned member must have CLEARED medical/fitness status");
            }

            if (personReadinessStatusRepository != null) {
                long unresolvedCount = personReadinessStatusRepository.countUnresolvedMandatoryRequirementsForExpedition(expeditionId);
                if (unresolvedCount > 0) {
                    throw new InvalidStateTransitionException("Cannot activate expedition: " + unresolvedCount + " mandatory pre-departure readiness requirements remain unverified");
                }
            }
        }

        expedition.setStatus(newStatus);
        return mapToDto(expeditionRepository.save(expedition));
    }

    @Transactional(readOnly = true)
    public ExpeditionDtos.ExpeditionDto getExpeditionById(UUID id) {
        Expedition expedition = expeditionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + id));
        return mapToDto(expedition);
    }

    @Transactional(readOnly = true)
    public List<ExpeditionDtos.ExpeditionDto> getAllExpeditions(ExpeditionStatus status) {
        List<Expedition> list = status != null ?
                expeditionRepository.findByStatus(status) :
                expeditionRepository.findAll();
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteExpedition(UUID id) {
        if (!expeditionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expedition not found with ID: " + id);
        }
        expeditionRepository.deleteById(id);
    }

    private void validateSequenceIntegrity(List<ExpeditionDtos.TransitLegRequest> legs) {
        if (legs == null || legs.isEmpty()) {
            throw new ValidationConflictException("Transit legs list cannot be empty");
        }

        List<Integer> orders = legs.stream()
                .map(ExpeditionDtos.TransitLegRequest::getSequenceOrder)
                .sorted()
                .collect(Collectors.toList());

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != i + 1) {
                throw new ValidationConflictException("Transit leg sequenceOrder must be contiguous starting from 1 (e.g., 1, 2, 3...)");
            }
        }
    }

    private ExpeditionDtos.ExpeditionDto mapToDto(Expedition e) {
        List<TransitLeg> legs = transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(e.getId());
        List<Person> personnel = personRepository.findByExpeditionId(e.getId());
        List<CargoDtos.CargoItemDto> cargo = cargoItemRepository.findByExpeditionId(e.getId()).stream()
                .map(c -> CargoDtos.CargoItemDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .category(c.getCategory())
                        .weightKg(c.getWeightKg())
                        .status(c.getStatus())
                        .expeditionId(e.getId())
                        .expeditionName(e.getName())
                        .build()
                ).collect(Collectors.toList());

        return ExpeditionDtos.ExpeditionDto.builder()
                .id(e.getId())
                .name(e.getName())
                .objective(e.getObjective())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .status(e.getStatus())
                .createdById(e.getCreatedBy() != null ? e.getCreatedBy().getId() : null)
                .createdByName(e.getCreatedBy() != null ? e.getCreatedBy().getUsername() : null)
                .transitLegs(legs.stream().map(l -> ExpeditionDtos.TransitLegDto.builder()
                        .id(l.getId())
                        .expeditionId(e.getId())
                        .sequenceOrder(l.getSequenceOrder())
                        .origin(l.getOrigin())
                        .destination(l.getDestination())
                        .mode(l.getMode())
                        .expectedDeparture(l.getExpectedDeparture())
                        .expectedArrival(l.getExpectedArrival())
                        .actualDeparture(l.getActualDeparture())
                        .actualArrival(l.getActualArrival())
                        .build()
                ).collect(Collectors.toList()))
                .personnel(personnel.stream().map(p -> PersonnelDtos.PersonDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .role(p.getRole())
                        .fitnessClearanceStatus(p.getFitnessClearanceStatus())
                        .currentStatus(p.getCurrentStatus())
                        .currentLocation(p.getCurrentLocation())
                        .expeditionId(e.getId())
                        .expeditionName(e.getName())
                        .build()
                ).collect(Collectors.toList()))
                .cargo(cargo)
                .build();
    }
}
