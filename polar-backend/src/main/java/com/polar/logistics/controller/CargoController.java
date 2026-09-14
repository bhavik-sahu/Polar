package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.CargoDtos;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.service.CargoTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cargo")
@Tag(name = "Cargo & Assets", description = "Cargo lifecycle tracking, stock duplicate pre-checks, and inventory linking")
public class CargoController {

    private final CargoTrackingService cargoTrackingService;

    public CargoController(CargoTrackingService cargoTrackingService) {
        this.cargoTrackingService = cargoTrackingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Log new cargo item (performs pre-check for existing inventory stock at destination)")
    public ResponseEntity<ApiResponse<CargoDtos.CargoItemDto>> createCargo(
            @Valid @RequestBody CargoDtos.CreateCargoRequest request) {
        CargoDtos.CargoItemDto dto = cargoTrackingService.createCargo(request);
        String msg = dto.getWarning() != null ? dto.getWarning() : "Cargo item logged successfully";
        return new ResponseEntity<>(ApiResponse.ok(msg, dto), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR', 'STATION_COMMANDER')")
    @Operation(summary = "Update cargo lifecycle status (auto-syncs to Inventory when STORED)")
    public ResponseEntity<ApiResponse<CargoDtos.CargoItemDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody CargoDtos.UpdateCargoStatusRequest request) {
        CargoDtos.CargoItemDto dto = cargoTrackingService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cargo status updated to " + request.getStatus(), dto));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Assign cargo item to an expedition")
    public ResponseEntity<ApiResponse<CargoDtos.CargoItemDto>> assignCargo(
            @PathVariable UUID id,
            @Valid @RequestBody CargoDtos.AssignCargoRequest request) {
        CargoDtos.CargoItemDto dto = cargoTrackingService.assignToExpedition(id, request.getExpeditionId());
        return ResponseEntity.ok(ApiResponse.ok("Cargo assigned to expedition", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cargo item by ID")
    public ResponseEntity<ApiResponse<CargoDtos.CargoItemDto>> getCargoById(@PathVariable UUID id) {
        CargoDtos.CargoItemDto dto = cargoTrackingService.getCargoById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping
    @Operation(summary = "List cargo items with optional expedition and status filters")
    public ResponseEntity<ApiResponse<List<CargoDtos.CargoItemDto>>> getAllCargo(
            @RequestParam(required = false) UUID expeditionId,
            @RequestParam(required = false) CargoStatus status) {
        List<CargoDtos.CargoItemDto> list = cargoTrackingService.getAllCargo(expeditionId, status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HQ_ADMIN')")
    @Operation(summary = "Delete a cargo item")
    public ResponseEntity<ApiResponse<Void>> deleteCargo(@PathVariable UUID id) {
        cargoTrackingService.deleteCargo(id);
        return ResponseEntity.ok(ApiResponse.ok("Cargo item deleted successfully", null));
    }
}
