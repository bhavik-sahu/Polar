package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.dto.StatusPingDtos;
import com.polar.logistics.entity.CargoItem;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.enums.PingEntityType;
import com.polar.logistics.entity.enums.PingSource;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.repository.CargoItemRepository;
import com.polar.logistics.repository.PersonRepository;
import com.polar.logistics.repository.StatusPingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StatusTrackingService {

    private static final Logger log = LoggerFactory.getLogger(StatusTrackingService.class);

    private final StatusPingRepository statusPingRepository;
    private final PersonRepository personRepository;
    private final CargoItemRepository cargoItemRepository;
    private final StatusProviderManager statusProviderManager;

    public StatusTrackingService(StatusPingRepository statusPingRepository,
                                 PersonRepository personRepository,
                                 CargoItemRepository cargoItemRepository,
                                 StatusProviderManager statusProviderManager) {
        this.statusPingRepository = statusPingRepository;
        this.personRepository = personRepository;
        this.cargoItemRepository = cargoItemRepository;
        this.statusProviderManager = statusProviderManager;
    }

    @Transactional
    public StatusPingDtos.StatusPingDto recordStatusPing(StatusPingDtos.StatusPingRequest request) {
        LocalDateTime pingTime = request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now();
        PingSource source = request.getSource() != null ? request.getSource() : PingSource.MANUAL;

        // 1. Build and persist to MongoDB (append-only time series)
        StatusPing ping = StatusPing.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .timestamp(pingTime)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .statusNote(request.getStatusNote())
                .source(source)
                .build();

        StatusPing savedPing = statusPingRepository.save(ping);

        // 2. Dispatch to status provider adapter
        statusProviderManager.dispatch(savedPing);

        // 3. Denormalize latest state to PostgreSQL relational entities for fast querying
        if (request.getEntityType() == PingEntityType.PERSON) {
            updatePersonLocation(request.getEntityId(), request.getLatitude(), request.getLongitude(), pingTime, request.getStatusNote());
        } else if (request.getEntityType() == PingEntityType.CARGO) {
            updateCargoLocation(request.getEntityId(), request.getLatitude(), request.getLongitude(), pingTime);
        }

        return mapToDto(savedPing);
    }

    private void updatePersonLocation(UUID personId, Double lat, Double lon, LocalDateTime time, String note) {
        personRepository.findById(personId).ifPresent(person -> {
            if (lat != null) person.setLastKnownLatitude(lat);
            if (lon != null) person.setLastKnownLongitude(lon);
            person.setLastPingTime(time);
            if (note != null && !note.isBlank()) {
                person.setCurrentLocation(note);
            }
            personRepository.save(person);
            log.debug("Updated current location for person: {}", person.getName());
        });
    }

    private void updateCargoLocation(UUID cargoId, Double lat, Double lon, LocalDateTime time) {
        cargoItemRepository.findById(cargoId).ifPresent(cargo -> {
            if (lat != null) cargo.setLastKnownLatitude(lat);
            if (lon != null) cargo.setLastKnownLongitude(lon);
            cargo.setLastPingTime(time);
            cargoItemRepository.save(cargo);
            log.debug("Updated current location for cargo item: {}", cargo.getName());
        });
    }

    public List<StatusPingDtos.StatusPingDto> getPingHistory(PingEntityType entityType, UUID entityId) {
        return statusPingRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public StatusPingDtos.StatusPingDto getLatestPing(PingEntityType entityType, UUID entityId) {
        return statusPingRepository.findFirstByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("No status ping found for " + entityType + " with ID: " + entityId));
    }

    private StatusPingDtos.StatusPingDto mapToDto(StatusPing ping) {
        return StatusPingDtos.StatusPingDto.builder()
                .id(ping.getId())
                .entityType(ping.getEntityType())
                .entityId(ping.getEntityId())
                .timestamp(ping.getTimestamp())
                .latitude(ping.getLatitude())
                .longitude(ping.getLongitude())
                .statusNote(ping.getStatusNote())
                .source(ping.getSource())
                .build();
    }
}
