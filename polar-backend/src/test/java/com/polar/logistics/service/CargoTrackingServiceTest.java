package com.polar.logistics.service;

import com.polar.logistics.dto.CargoDtos;
import com.polar.logistics.entity.CargoItem;
import com.polar.logistics.entity.InventoryItem;
import com.polar.logistics.entity.enums.CargoCategory;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.entity.enums.StationLocation;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.exception.InvalidStateTransitionException;
import com.polar.logistics.repository.CargoItemRepository;
import com.polar.logistics.repository.ExpeditionRepository;
import com.polar.logistics.repository.InventoryItemRepository;
import com.polar.logistics.repository.TransitLegRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoTrackingServiceTest {

    @Mock
    private CargoItemRepository cargoItemRepository;

    @Mock
    private ExpeditionRepository expeditionRepository;

    @Mock
    private TransitLegRepository transitLegRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private CargoTrackingService cargoTrackingService;

    private UUID cargoId;
    private CargoItem cargoItem;

    @BeforeEach
    void setUp() {
        cargoId = UUID.randomUUID();
        cargoItem = CargoItem.builder()
                .id(cargoId)
                .name("Medical Oxygen Cylinders")
                .category(CargoCategory.GENERAL)
                .weightKg(new BigDecimal("150.00"))
                .status(CargoStatus.PACKED)
                .currentStationLocation(StationLocation.INDIA)
                .build();
    }

    @Test
    @DisplayName("Should detect existing stock at destination and attach duplicate warning on creation")
    void shouldAttachDuplicateStockWarning() {
        CargoDtos.CreateCargoRequest request = CargoDtos.CreateCargoRequest.builder()
                .name("Diesel Fuel")
                .category(CargoCategory.GENERAL)
                .weightKg(new BigDecimal("500.00"))
                .destinationStation(StationLocation.MAITRI)
                .build();

        InventoryItem existingItem = InventoryItem.builder()
                .name("Diesel Fuel")
                .station(StationName.MAITRI)
                .quantity(4000)
                .unit("Litres")
                .build();

        when(inventoryItemRepository.findByNameIgnoreCaseAndCategoryIgnoreCaseAndStation("Diesel Fuel", "GENERAL", StationName.MAITRI))
                .thenReturn(Optional.of(existingItem));

        when(cargoItemRepository.save(any(CargoItem.class))).thenAnswer(invocation -> {
            CargoItem c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CargoDtos.CargoItemDto dto = cargoTrackingService.createCargo(request);

        assertNotNull(dto);
        assertNotNull(dto.getWarning());
        assertTrue(dto.getWarning().contains("Duplicate Stock Notice"));
    }

    @Test
    @DisplayName("Should reject illegal status jump from PACKED directly to STORED")
    void shouldRejectIllegalStatusTransition() {
        when(cargoItemRepository.findById(cargoId)).thenReturn(Optional.of(cargoItem));

        CargoDtos.UpdateCargoStatusRequest request = CargoDtos.UpdateCargoStatusRequest.builder()
                .status(CargoStatus.STORED)
                .build();

        assertThrows(InvalidStateTransitionException.class, () ->
                cargoTrackingService.updateStatus(cargoId, request));
    }

    @Test
    @DisplayName("Should increment inventory item quantity when cargo status becomes STORED")
    void shouldSyncInventoryOnStoredTransition() {
        cargoItem.setStatus(CargoStatus.ARRIVED);
        cargoItem.setCurrentStationLocation(StationLocation.MAITRI);

        when(cargoItemRepository.findById(cargoId)).thenReturn(Optional.of(cargoItem));

        InventoryItem inventoryItem = InventoryItem.builder()
                .id(UUID.randomUUID())
                .name("Medical Oxygen Cylinders")
                .station(StationName.MAITRI)
                .quantity(50)
                .unit("kg")
                .build();

        when(inventoryItemRepository.findByNameIgnoreCaseAndStation("Medical Oxygen Cylinders", StationName.MAITRI))
                .thenReturn(Optional.of(inventoryItem));

        when(cargoItemRepository.save(any(CargoItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CargoDtos.UpdateCargoStatusRequest request = CargoDtos.UpdateCargoStatusRequest.builder()
                .status(CargoStatus.STORED)
                .stationLocation(StationLocation.MAITRI)
                .build();

        CargoDtos.CargoItemDto result = cargoTrackingService.updateStatus(cargoId, request);

        assertNotNull(result);
        assertEquals(CargoStatus.STORED, result.getStatus());
        // 50 initial + 150 from cargo weight = 200
        assertEquals(200, inventoryItem.getQuantity());
        verify(inventoryItemRepository, times(1)).save(inventoryItem);
    }
}
