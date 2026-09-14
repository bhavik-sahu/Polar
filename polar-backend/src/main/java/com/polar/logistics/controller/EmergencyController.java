package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.EmergencyDtos;
import com.polar.logistics.security.UserPrincipal;
import com.polar.logistics.service.EmergencyResponseService;
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
@RequestMapping("/api/v1/emergencies")
@Tag(name = "Emergency Response", description = "SOS incident intake, persistent banner alerts, action logs, and resolution workflows")
public class EmergencyController {

    private final EmergencyResponseService emergencyResponseService;

    public EmergencyController(EmergencyResponseService emergencyResponseService) {
        this.emergencyResponseService = emergencyResponseService;
    }

    @PostMapping("/sos")
    @Operation(summary = "Trigger manual SOS emergency alert (accessible to all members)")
    public ResponseEntity<ApiResponse<EmergencyDtos.EmergencyIncidentDto>> triggerSos(
            @Valid @RequestBody EmergencyDtos.ManualSosRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        EmergencyDtos.EmergencyIncidentDto incident = emergencyResponseService.triggerManualSos(request, principal);
        return new ResponseEntity<>(ApiResponse.ok("EMERGENCY SOS ALERT ACTIVATED", incident), HttpStatus.CREATED);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active emergencies for persistent mission control alert banner")
    public ResponseEntity<ApiResponse<List<EmergencyDtos.EmergencyIncidentDto>>> getActiveEmergencies() {
        List<EmergencyDtos.EmergencyIncidentDto> active = emergencyResponseService.getActiveEmergencies();
        return ResponseEntity.ok(ApiResponse.ok(active));
    }

    @GetMapping
    @Operation(summary = "Get all emergency incidents history")
    public ResponseEntity<ApiResponse<List<EmergencyDtos.EmergencyIncidentDto>>> getAllEmergencies() {
        List<EmergencyDtos.EmergencyIncidentDto> list = emergencyResponseService.getAllEmergencies();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get emergency incident details by ID")
    public ResponseEntity<ApiResponse<EmergencyDtos.EmergencyIncidentDto>> getEmergencyById(@PathVariable UUID id) {
        EmergencyDtos.EmergencyIncidentDto dto = emergencyResponseService.getEmergencyById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR', 'STATION_COMMANDER')")
    @Operation(summary = "Resolve active emergency incident with resolution notes")
    public ResponseEntity<ApiResponse<EmergencyDtos.EmergencyIncidentDto>> resolveEmergency(
            @PathVariable UUID id,
            @Valid @RequestBody EmergencyDtos.ResolveEmergencyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        EmergencyDtos.EmergencyIncidentDto resolved = emergencyResponseService.resolveEmergency(id, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Emergency incident marked as RESOLVED", resolved));
    }

    @PostMapping("/{id}/response-log")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR', 'STATION_COMMANDER')")
    @Operation(summary = "Append an action log entry to an emergency incident")
    public ResponseEntity<ApiResponse<EmergencyDtos.EmergencyResponseLogDto>> addResponseLog(
            @PathVariable UUID id,
            @Valid @RequestBody EmergencyDtos.EmergencyResponseLogRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        EmergencyDtos.EmergencyResponseLogDto logEntry = emergencyResponseService.addResponseLog(id, request, principal);
        return new ResponseEntity<>(ApiResponse.ok("Response action logged", logEntry), HttpStatus.CREATED);
    }
}
