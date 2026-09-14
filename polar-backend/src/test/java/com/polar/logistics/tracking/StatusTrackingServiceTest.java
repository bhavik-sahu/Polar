package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.dto.StatusPingDtos;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.enums.PingEntityType;
import com.polar.logistics.entity.enums.PingSource;
import com.polar.logistics.repository.CargoItemRepository;
import com.polar.logistics.repository.PersonRepository;
import com.polar.logistics.repository.StatusPingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusTrackingServiceTest {

    @Mock
    private StatusPingRepository statusPingRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CargoItemRepository cargoItemRepository;

    @Mock
    private StatusProviderManager statusProviderManager;

    @InjectMocks
    private StatusTrackingService statusTrackingService;

    private UUID personId;
    private Person person;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        person = Person.builder()
                .id(personId)
                .name("Dr. S. K. Raman")
                .build();
    }

    @Test
    @DisplayName("Should persist StatusPing in MongoDB and denormalize coordinates to Person entity")
    void shouldPersistPingAndDenormalizeLocation() {
        StatusPingDtos.StatusPingRequest request = StatusPingDtos.StatusPingRequest.builder()
                .entityType(PingEntityType.PERSON)
                .entityId(personId)
                .latitude(-70.767)
                .longitude(11.733)
                .statusNote("Arrived at Maitri station container #4")
                .source(PingSource.MANUAL)
                .timestamp(LocalDateTime.now())
                .build();

        when(statusPingRepository.save(any(StatusPing.class))).thenAnswer(invocation -> {
            StatusPing p = invocation.getArgument(0);
            p.setId("mongo-ping-123");
            return p;
        });

        when(personRepository.findById(personId)).thenReturn(Optional.of(person));

        StatusPingDtos.StatusPingDto result = statusTrackingService.recordStatusPing(request);

        assertNotNull(result);
        assertEquals("mongo-ping-123", result.getId());
        assertEquals(-70.767, result.getLatitude());
        assertEquals(11.733, result.getLongitude());

        // Verify that Person entity in relational DB got updated with coordinates and ping time
        assertEquals(-70.767, person.getLastKnownLatitude());
        assertEquals(11.733, person.getLastKnownLongitude());
        assertEquals("Arrived at Maitri station container #4", person.getCurrentLocation());
        assertNotNull(person.getLastPingTime());
        verify(personRepository, times(1)).save(person);
        verify(statusProviderManager, times(1)).dispatch(any(StatusPing.class));
    }
}
