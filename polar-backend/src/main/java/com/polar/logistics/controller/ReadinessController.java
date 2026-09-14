package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.ReadinessDtos;
import com.polar.logistics.service.ReadinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Pre-Departure Readiness", description = "Expedition crew readiness matrix, checklist verification, and pre-departure alerts")
public class ReadinessController {

    private final ReadinessService readinessService;

    public ReadinessController(ReadinessService readinessService) {
        this.readinessService = readinessService;
    }

    @GetMapping("/expeditions/{id}/readiness")
    @Operation(summary = "Get full expedition crew pre-departure readiness matrix and stats")
    public ResponseEntity<ApiResponse<ReadinessDtos.ExpeditionReadinessDto>> getExpeditionReadiness(@PathVariable UUID id) {
        ReadinessDtos.ExpeditionReadinessDto dto = readinessService.getExpeditionReadiness(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PatchMapping("/readiness/{statusId}")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'STATION_COMMANDER', 'LOGISTICS_COORDINATOR', 'EXPEDITION_MEMBER')")
    @Operation(summary = "Update status of a specific crew readiness requirement (e.g., verify, submit, or reject)")
    public ResponseEntity<ApiResponse<ReadinessDtos.PersonRequirementStatusDto>> updateReadinessStatus(
            @PathVariable UUID statusId,
            @Valid @RequestBody ReadinessDtos.UpdateReadinessStatusRequest request,
            Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        ReadinessDtos.PersonRequirementStatusDto dto = readinessService.updateRequirementStatus(statusId, request, username);
        return ResponseEntity.ok(ApiResponse.ok("Readiness status updated to " + request.getStatus(), dto));
    }

    @GetMapping("/readiness-requirements")
    @Operation(summary = "List all configured pre-departure readiness requirements")
    public ResponseEntity<ApiResponse<List<ReadinessDtos.RequirementDto>>> getAllRequirements() {
        List<ReadinessDtos.RequirementDto> list = readinessService.getAllRequirements();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @PostMapping("/readiness-requirements")
    @PreAuthorize("hasRole('HQ_ADMIN')")
    @Operation(summary = "Define a new pre-departure readiness requirement (HQ Admin only)")
    public ResponseEntity<ApiResponse<ReadinessDtos.RequirementDto>> createRequirement(
            @Valid @RequestBody ReadinessDtos.CreateRequirementRequest request) {
        ReadinessDtos.RequirementDto dto = readinessService.createRequirement(request);
        return new ResponseEntity<>(ApiResponse.ok("Readiness requirement created", dto), HttpStatus.CREATED);
    }
}
