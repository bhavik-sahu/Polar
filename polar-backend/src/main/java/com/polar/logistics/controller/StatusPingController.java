package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.StatusPingDtos;
import com.polar.logistics.entity.enums.PingEntityType;
import com.polar.logistics.tracking.StatusTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/status-pings")
@Tag(name = "Status Tracking", description = "Universal tracking pings, telemetry ingestion, and timeline history")
public class StatusPingController {

    private final StatusTrackingService statusTrackingService;

    public StatusPingController(StatusTrackingService statusTrackingService) {
        this.statusTrackingService = statusTrackingService;
    }

    @PostMapping
    @Operation(summary = "Record a status ping (persists in MongoDB and denormalizes to relational entity)")
    public ResponseEntity<ApiResponse<StatusPingDtos.StatusPingDto>> recordPing(
            @Valid @RequestBody StatusPingDtos.StatusPingRequest request) {
        StatusPingDtos.StatusPingDto dto = statusTrackingService.recordStatusPing(request);
        return new ResponseEntity<>(ApiResponse.ok("Status ping recorded successfully", dto), HttpStatus.CREATED);
    }

    @GetMapping("/{entityType}/{entityId}/history")
    @Operation(summary = "Get full historical tracking pings from MongoDB for timeline views")
    public ResponseEntity<ApiResponse<List<StatusPingDtos.StatusPingDto>>> getPingHistory(
            @PathVariable PingEntityType entityType,
            @PathVariable UUID entityId) {
        List<StatusPingDtos.StatusPingDto> history = statusTrackingService.getPingHistory(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/{entityType}/{entityId}/latest")
    @Operation(summary = "Get latest status ping for entity")
    public ResponseEntity<ApiResponse<StatusPingDtos.StatusPingDto>> getLatestPing(
            @PathVariable PingEntityType entityType,
            @PathVariable UUID entityId) {
        StatusPingDtos.StatusPingDto latest = statusTrackingService.getLatestPing(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.ok(latest));
    }
}
