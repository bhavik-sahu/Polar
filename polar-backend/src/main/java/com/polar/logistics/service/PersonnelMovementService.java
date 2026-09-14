package com.polar.logistics.service;

import com.polar.logistics.dto.PersonnelDtos;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.TransitLeg;
import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.entity.enums.PersonStatus;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.repository.ExpeditionRepository;
import com.polar.logistics.repository.PersonRepository;
import com.polar.logistics.repository.TransitLegRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonnelMovementService {

    private final PersonRepository personRepository;
    private final ExpeditionRepository expeditionRepository;
    private final TransitLegRepository transitLegRepository;

    public PersonnelMovementService(PersonRepository personRepository,
                                    ExpeditionRepository expeditionRepository,
                                    TransitLegRepository transitLegRepository) {
        this.personRepository = personRepository;
        this.expeditionRepository = expeditionRepository;
        this.transitLegRepository = transitLegRepository;
    }

    @Transactional
    public PersonnelDtos.PersonDto createPerson(PersonnelDtos.CreatePersonRequest request) {
        Expedition expedition = null;
        if (request.getExpeditionId() != null) {
            expedition = expeditionRepository.findById(request.getExpeditionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + request.getExpeditionId()));
        }

        TransitLeg transitLeg = null;
        if (request.getCurrentTransitLegId() != null) {
            transitLeg = transitLegRepository.findById(request.getCurrentTransitLegId())
                    .orElseThrow(() -> new ResourceNotFoundException("Transit leg not found with ID: " + request.getCurrentTransitLegId()));
        }

        Person person = Person.builder()
                .name(request.getName())
                .role(request.getRole())
                .fitnessClearanceStatus(request.getFitnessClearanceStatus() != null ? request.getFitnessClearanceStatus() : FitnessClearanceStatus.PENDING)
                .currentStatus(request.getCurrentStatus() != null ? request.getCurrentStatus() : PersonStatus.IN_INDIA)
                .currentLocation(request.getCurrentLocation() != null ? request.getCurrentLocation() : "India HQ / Transit")
                .expedition(expedition)
                .currentTransitLeg(transitLeg)
                .build();

        return mapToDto(personRepository.save(person));
    }

    @Transactional
    public PersonnelDtos.PersonDto assignExpedition(UUID personId, UUID expeditionId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        person.setExpedition(expedition);
        return mapToDto(personRepository.save(person));
    }

    @Transactional
    public PersonnelDtos.PersonDto updatePersonStatus(UUID personId, PersonnelDtos.UpdatePersonStatusRequest request) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        person.setCurrentStatus(request.getCurrentStatus());
        if (request.getCurrentLocation() != null) {
            person.setCurrentLocation(request.getCurrentLocation());
        }

        if (request.getCurrentTransitLegId() != null) {
            TransitLeg leg = transitLegRepository.findById(request.getCurrentTransitLegId()).orElse(null);
            person.setCurrentTransitLeg(leg);
        } else if (request.getCurrentStatus() == PersonStatus.AT_STATION || request.getCurrentStatus() == PersonStatus.IN_INDIA) {
            person.setCurrentTransitLeg(null);
        }

        return mapToDto(personRepository.save(person));
    }

    @Transactional
    public PersonnelDtos.PersonDto updateFitnessStatus(UUID personId, FitnessClearanceStatus status) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personId));

        person.setFitnessClearanceStatus(status);
        return mapToDto(personRepository.save(person));
    }

    @Transactional(readOnly = true)
    public List<PersonnelDtos.PersonDto> getRoster(UUID expeditionId, StationName station) {
        List<Person> list;
        if (expeditionId != null) {
            list = personRepository.findByExpeditionId(expeditionId);
        } else {
            list = personRepository.findAll();
        }

        if (station != null) {
            list = list.stream()
                    .filter(p -> p.getCurrentLocation() != null && p.getCurrentLocation().toUpperCase().contains(station.name()))
                    .collect(Collectors.toList());
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonnelDtos.PersonDto getPersonById(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));
        return mapToDto(person);
    }

    @Transactional(readOnly = true)
    public PersonnelDtos.HeadcountSummaryDto getHeadcountSummary() {
        List<Person> all = personRepository.findAll();

        Map<String, Map<String, Long>> stationBreakdown = new HashMap<>();
        stationBreakdown.put("Maitri", new HashMap<>());
        stationBreakdown.put("Bharati", new HashMap<>());
        stationBreakdown.put("Transit", new HashMap<>());

        for (Person p : all) {
            String stationKey = "Transit";
            if (p.getCurrentLocation() != null) {
                if (p.getCurrentLocation().toUpperCase().contains("MAITRI")) stationKey = "Maitri";
                else if (p.getCurrentLocation().toUpperCase().contains("BHARATI")) stationKey = "Bharati";
            }

            String statusKey = p.getCurrentStatus().name();
            stationBreakdown.get(stationKey).put(statusKey, stationBreakdown.get(stationKey).getOrDefault(statusKey, 0L) + 1);
        }

        return PersonnelDtos.HeadcountSummaryDto.builder()
                .stationBreakdown(stationBreakdown)
                .totalPersonnel(all.size())
                .build();
    }

    @Transactional
    public void deletePerson(UUID id) {
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found with ID: " + id);
        }
        personRepository.deleteById(id);
    }

    private PersonnelDtos.PersonDto mapToDto(Person p) {
        return PersonnelDtos.PersonDto.builder()
                .id(p.getId())
                .name(p.getName())
                .role(p.getRole())
                .fitnessClearanceStatus(p.getFitnessClearanceStatus())
                .currentStatus(p.getCurrentStatus())
                .currentLocation(p.getCurrentLocation())
                .currentTransitLegId(p.getCurrentTransitLeg() != null ? p.getCurrentTransitLeg().getId() : null)
                .currentTransitLegInfo(p.getCurrentTransitLeg() != null ?
                        p.getCurrentTransitLeg().getOrigin() + " → " + p.getCurrentTransitLeg().getDestination() : null)
                .expeditionId(p.getExpedition() != null ? p.getExpedition().getId() : null)
                .expeditionName(p.getExpedition() != null ? p.getExpedition().getName() : null)
                .lastKnownLatitude(p.getLastKnownLatitude())
                .lastKnownLongitude(p.getLastKnownLongitude())
                .lastPingTime(p.getLastPingTime())
                .build();
    }
}
