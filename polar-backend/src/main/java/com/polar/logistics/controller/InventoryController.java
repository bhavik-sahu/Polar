package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.InventoryDtos;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.security.UserPrincipal;
import com.polar.logistics.service.InventoryManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Station inventory management, stock adjustments, and low stock / expiry alerts")
public class InventoryController {

    private final InventoryManagementService inventoryManagementService;

    public InventoryController(InventoryManagementService inventoryManagementService) {
        this.inventoryManagementService = inventoryManagementService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR', 'STATION_COMMANDER')")
    @Operation(summary = "Add a new inventory item to station stock")
    public ResponseEntity<ApiResponse<InventoryDtos.InventoryItemDto>> createItem(
            @Valid @RequestBody InventoryDtos.CreateInventoryItemRequest request) {
        InventoryDtos.InventoryItemDto dto = inventoryManagementService.createInventoryItem(request);
        return new ResponseEntity<>(ApiResponse.ok("Inventory item created", dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR', 'STATION_COMMANDER')")
    @Operation(summary = "Adjust inventory stock quantity with audit logging and non-negative checks")
    public ResponseEntity<ApiResponse<InventoryDtos.InventoryItemDto>> adjustItem(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryDtos.AdjustInventoryRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        InventoryDtos.InventoryItemDto dto = inventoryManagementService.adjustInventory(id, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Inventory adjusted successfully", dto));
    }

    @GetMapping
    @Operation(summary = "Get station inventory sorted by urgency (below-threshold items first)")
    public ResponseEntity<ApiResponse<List<InventoryDtos.InventoryItemDto>>> getAllInventory(
            @RequestParam(required = false) StationName station) {
        List<InventoryDtos.InventoryItemDto> items = inventoryManagementService.getAllInventory(station);
        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get low stock and near-expiry alerts")
    public ResponseEntity<ApiResponse<List<InventoryDtos.InventoryAlertDto>>> getAlerts() {
        List<InventoryDtos.InventoryAlertDto> alerts = inventoryManagementService.getInventoryAlerts();
        return ResponseEntity.ok(ApiResponse.ok(alerts));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by ID")
    public ResponseEntity<ApiResponse<InventoryDtos.InventoryItemDto>> getInventoryById(@PathVariable UUID id) {
        InventoryDtos.InventoryItemDto item = inventoryManagementService.getInventoryById(id);
        return ResponseEntity.ok(ApiResponse.ok(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HQ_ADMIN')")
    @Operation(summary = "Delete an inventory item")
    public ResponseEntity<ApiResponse<Void>> deleteInventoryItem(@PathVariable UUID id) {
        inventoryManagementService.deleteInventoryItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Inventory item deleted successfully", null));
    }
}
