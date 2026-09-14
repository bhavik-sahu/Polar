package com.polar.logistics.service;

import com.polar.logistics.dto.InventoryDtos;
import com.polar.logistics.entity.InventoryItem;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.exception.ValidationConflictException;
import com.polar.logistics.repository.InventoryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryManagementServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private InventoryManagementService inventoryManagementService;

    private UUID itemId;
    private InventoryItem item;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        item = InventoryItem.builder()
                .id(itemId)
                .name("Polar Diesel Fuel")
                .category("FUEL")
                .station(StationName.MAITRI)
                .quantity(100)
                .unit("Litres")
                .reorderThreshold(20)
                .build();
    }

    @Test
    @DisplayName("Should reject adjustment if resulting quantity is negative")
    void shouldRejectNegativeInventoryAdjustment() {
        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        InventoryDtos.AdjustInventoryRequest request = InventoryDtos.AdjustInventoryRequest.builder()
                .changeQuantity(-150) // 100 - 150 = -50 (illegal)
                .reason("Excess consumption test")
                .build();

        assertThrows(ValidationConflictException.class, () ->
                inventoryManagementService.adjustInventory(itemId, request, null));
    }

    @Test
    @DisplayName("Should successfully adjust quantity when valid")
    void shouldAdjustInventorySuccessfully() {
        when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryDtos.AdjustInventoryRequest request = InventoryDtos.AdjustInventoryRequest.builder()
                .changeQuantity(-30) // 100 - 30 = 70
                .reason("Generator fuel consumption")
                .build();

        InventoryDtos.InventoryItemDto result = inventoryManagementService.adjustInventory(itemId, request, null);

        assertNotNull(result);
        assertEquals(70, result.getQuantity());
    }
}
