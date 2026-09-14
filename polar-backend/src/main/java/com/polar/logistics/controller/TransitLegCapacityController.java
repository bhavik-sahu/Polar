package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.CapacityDtos;
import com.polar.logistics.service.TransitLegCapacityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Cargo-to-Capacity Optimizer", description = "Real-time vessel and transit leg cargo capacity optimization")
public class TransitLegCapacityController {

    private final TransitLegCapacityService capacityService;

    public TransitLegCapacityController(TransitLegCapacityService capacityService) {
        this.capacityService = capacityService;
    }

    @GetMapping("/transit-legs/{id}/capacity")
    @Operation(summary = "Get live capacity utilization status for a transit leg")
    public ResponseEntity<ApiResponse<CapacityDtos.LegCapacityDto>> getLegCapacity(@PathVariable UUID id) {
        CapacityDtos.LegCapacityDto dto = capacityService.getCapacityStatus(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping("/expeditions/{id}/capacity-overview")
    @Operation(summary = "Get expedition-wide cargo capacity overview across all transit legs")
    public ResponseEntity<ApiResponse<CapacityDtos.ExpeditionCapacityOverviewDto>> getExpeditionCapacityOverview(@PathVariable UUID id) {
        CapacityDtos.ExpeditionCapacityOverviewDto dto = capacityService.getExpeditionCapacityOverview(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PatchMapping("/transit-legs/{id}/capacity")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Set or update transit leg max weight/volume capacity")
    public ResponseEntity<ApiResponse<CapacityDtos.LegCapacityDto>> updateCapacity(
            @PathVariable UUID id,
            @Valid @RequestBody CapacityDtos.UpdateCapacityRequest request) {
        CapacityDtos.LegCapacityDto dto = capacityService.updateLegCapacity(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Transit leg capacity updated successfully", dto));
    }

    @PostMapping("/transit-legs/{id}/capacity-check")
    @Operation(summary = "Advisory pre-check for adding cargo to a transit leg without committing")
    public ResponseEntity<ApiResponse<CapacityDtos.CapacityCheckResponse>> checkCapacity(
            @PathVariable UUID id,
            @Valid @RequestBody CapacityDtos.CapacityCheckRequest request) {
        CapacityDtos.CapacityCheckResponse response = capacityService.checkCapacityPreCheck(id, request.getAdditionalWeightKg());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
