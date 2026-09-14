package com.polar.logistics.service;

import com.polar.logistics.dto.ExpeditionDtos;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.TransitLeg;
import com.polar.logistics.entity.enums.ExpeditionStatus;
import com.polar.logistics.entity.enums.FitnessClearanceStatus;
import com.polar.logistics.entity.enums.TransitMode;
import com.polar.logistics.exception.InvalidStateTransitionException;
import com.polar.logistics.exception.ValidationConflictException;
import com.polar.logistics.repository.CargoItemRepository;
import com.polar.logistics.repository.ExpeditionRepository;
import com.polar.logistics.repository.PersonReadinessStatusRepository;
import com.polar.logistics.repository.PersonRepository;
import com.polar.logistics.repository.TransitLegRepository;
import com.polar.logistics.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpeditionPlanningServiceTest {

    @Mock
    private ExpeditionRepository expeditionRepository;

    @Mock
    private TransitLegRepository transitLegRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private CargoItemRepository cargoItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PersonReadinessStatusRepository personReadinessStatusRepository;

    @InjectMocks
    private ExpeditionPlanningService expeditionPlanningService;

    private UUID expeditionId;
    private Expedition expedition;

    @BeforeEach
    void setUp() {
        expeditionId = UUID.randomUUID();
        expedition = Expedition.builder()
                .id(expeditionId)
                .name("47-ISEA Test Expedition")
                .startDate(LocalDate.of(2026, 11, 1))
                .endDate(LocalDate.of(2027, 4, 30))
                .status(ExpeditionStatus.PLANNED)
                .build();
    }

    @Test
    @DisplayName("Should throw ValidationConflictException when transit legs sequence is not contiguous")
    void shouldRejectNonContiguousTransitLegSequence() {
        ExpeditionDtos.CreateExpeditionRequest request = ExpeditionDtos.CreateExpeditionRequest.builder()
                .name("Test Non-Contiguous")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .transitLegs(List.of(
                        ExpeditionDtos.TransitLegRequest.builder()
                                .sequenceOrder(1)
                                .origin("Goa").destination("Cape Town").mode(TransitMode.FLIGHT)
                                .expectedDeparture(LocalDateTime.now()).expectedArrival(LocalDateTime.now().plusDays(2))
                                .build(),
                        ExpeditionDtos.TransitLegRequest.builder()
                                .sequenceOrder(3) // Gap! 2 is missing
                                .origin("Cape Town").destination("Maitri").mode(TransitMode.SHIP)
                                .expectedDeparture(LocalDateTime.now().plusDays(3)).expectedArrival(LocalDateTime.now().plusDays(15))
                                .build()
                ))
                .build();

        assertThrows(ValidationConflictException.class, () ->
                expeditionPlanningService.createExpedition(request, null));
    }

    @Test
    @DisplayName("Should reject activation if expedition has no transit legs")
    void shouldRejectActivationWithoutTransitLegs() {
        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(expeditionId)).thenReturn(Collections.emptyList());

        assertThrows(InvalidStateTransitionException.class, () ->
                expeditionPlanningService.updateStatus(expeditionId, ExpeditionStatus.ACTIVE));
    }

    @Test
    @DisplayName("Should reject activation if no assigned person is CLEARED")
    void shouldRejectActivationWithoutClearedPersonnel() {
        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(expeditionId)).thenReturn(List.of(
                TransitLeg.builder().id(UUID.randomUUID()).sequenceOrder(1).build()
        ));
        when(personRepository.findByExpeditionId(expeditionId)).thenReturn(List.of(
                Person.builder().id(UUID.randomUUID()).fitnessClearanceStatus(FitnessClearanceStatus.PENDING).build()
        ));

        assertThrows(InvalidStateTransitionException.class, () ->
                expeditionPlanningService.updateStatus(expeditionId, ExpeditionStatus.ACTIVE));
    }

    @Test
    @DisplayName("Should activate expedition when legs and cleared personnel are present")
    void shouldActivateExpeditionSuccessfully() {
        when(expeditionRepository.findById(expeditionId)).thenReturn(Optional.of(expedition));
        when(transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(expeditionId)).thenReturn(List.of(
                TransitLeg.builder().id(UUID.randomUUID()).sequenceOrder(1).origin("Goa").destination("Maitri").mode(TransitMode.SHIP)
                        .expectedDeparture(LocalDateTime.now()).expectedArrival(LocalDateTime.now().plusDays(10)).build()
        ));
        when(personRepository.findByExpeditionId(expeditionId)).thenReturn(List.of(
                Person.builder().id(UUID.randomUUID()).fitnessClearanceStatus(FitnessClearanceStatus.CLEARED).build()
        ));
        when(expeditionRepository.save(any(Expedition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpeditionDtos.ExpeditionDto result = expeditionPlanningService.updateStatus(expeditionId, ExpeditionStatus.ACTIVE);

        assertNotNull(result);
        assertEquals(ExpeditionStatus.ACTIVE, result.getStatus());
    }
}
