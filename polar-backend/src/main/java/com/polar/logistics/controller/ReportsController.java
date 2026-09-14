package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.service.ReportsExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports & Exports", description = "Expedition manifests and aggregated logistics reporting")
public class ReportsController {

    private final ReportsExportService reportsExportService;

    public ReportsController(ReportsExportService reportsExportService) {
        this.reportsExportService = reportsExportService;
    }

    @GetMapping("/expedition-summary/{id}")
    @Operation(summary = "Get aggregated expedition summary report with transit legs, crew roster, and cargo manifest")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExpeditionSummary(@PathVariable UUID id) {
        Map<String, Object> report = reportsExportService.getExpeditionSummaryReport(id);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/expeditions")
    @Operation(summary = "Get aggregated summary of all expeditions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllExpeditionsSummary() {
        Map<String, Object> report = reportsExportService.getAllExpeditionsReport();
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/cargo-manifest")
    @Operation(summary = "Get filtered cargo manifest report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCargoManifest(
            @RequestParam(required = false, defaultValue = "ALL") String station,
            @RequestParam(required = false, defaultValue = "ALL") String status) {
        Map<String, Object> report = reportsExportService.getCargoManifestReport(station, status);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/inventory-snapshot")
    @Operation(summary = "Get station inventory snapshot report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInventorySnapshot(
            @RequestParam(required = false, defaultValue = "ALL") String station) {
        Map<String, Object> report = reportsExportService.getInventorySnapshotReport(station);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/personnel-roster")
    @Operation(summary = "Get personnel and medical roster report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPersonnelRoster(
            @RequestParam(required = false, defaultValue = "ALL") String station,
            @RequestParam(required = false) UUID expeditionId) {
        Map<String, Object> report = reportsExportService.getPersonnelRosterReport(station, expeditionId);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get system operational and emergency audit logs report")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuditLogs() {
        Map<String, Object> report = reportsExportService.getOperationalAuditReport();
        return ResponseEntity.ok(ApiResponse.ok(report));
    }
}
