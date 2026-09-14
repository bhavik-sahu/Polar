package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.ExpeditionDtos;
import com.polar.logistics.entity.enums.ExpeditionStatus;
import com.polar.logistics.security.UserPrincipal;
import com.polar.logistics.service.ExpeditionPlanningService;
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
@RequestMapping("/api/v1/expeditions")
@Tag(name = "Expeditions", description = "Expedition planning, transit legs, and lifecycle management")
public class ExpeditionController {

    private final ExpeditionPlanningService expeditionPlanningService;

    public ExpeditionController(ExpeditionPlanningService expeditionPlanningService) {
        this.expeditionPlanningService = expeditionPlanningService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Create an expedition with initial transit legs atomically")
    public ResponseEntity<ApiResponse<ExpeditionDtos.ExpeditionDto>> createExpedition(
            @Valid @RequestBody ExpeditionDtos.CreateExpeditionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ExpeditionDtos.ExpeditionDto created = expeditionPlanningService.createExpedition(request, principal);
        return new ResponseEntity<>(ApiResponse.ok("Expedition created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/legs")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Reorder or update transit legs with sequence integrity checks")
    public ResponseEntity<ApiResponse<ExpeditionDtos.ExpeditionDto>> updateTransitLegs(
            @PathVariable UUID id,
            @Valid @RequestBody ExpeditionDtos.UpdateTransitLegsRequest request) {
        ExpeditionDtos.ExpeditionDto updated = expeditionPlanningService.updateTransitLegs(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Transit legs updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Update expedition status (validates leg existence and cleared personnel on activation)")
    public ResponseEntity<ApiResponse<ExpeditionDtos.ExpeditionDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ExpeditionDtos.UpdateExpeditionStatusRequest request) {
        ExpeditionDtos.ExpeditionDto updated = expeditionPlanningService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Expedition status updated to " + request.getStatus(), updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expedition details with nested transit legs, roster, and cargo")
    public ResponseEntity<ApiResponse<ExpeditionDtos.ExpeditionDto>> getExpeditionById(@PathVariable UUID id) {
        ExpeditionDtos.ExpeditionDto dto = expeditionPlanningService.getExpeditionById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @GetMapping
    @Operation(summary = "List expeditions with optional status filter")
    public ResponseEntity<ApiResponse<List<ExpeditionDtos.ExpeditionDto>>> getAllExpeditions(
            @RequestParam(required = false) ExpeditionStatus status) {
        List<ExpeditionDtos.ExpeditionDto> list = expeditionPlanningService.getAllExpeditions(status);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HQ_ADMIN')")
    @Operation(summary = "Delete an expedition")
    public ResponseEntity<ApiResponse<Void>> deleteExpedition(@PathVariable UUID id) {
        expeditionPlanningService.deleteExpedition(id);
        return ResponseEntity.ok(ApiResponse.ok("Expedition deleted successfully", null));
    }
}
