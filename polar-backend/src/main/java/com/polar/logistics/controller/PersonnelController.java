package com.polar.logistics.controller;

import com.polar.logistics.dto.ApiResponse;
import com.polar.logistics.dto.PersonnelDtos;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.service.PersonnelMovementService;
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
@RequestMapping("/api/v1/personnel")
@Tag(name = "Personnel", description = "Personnel movement tracking, expedition assignment, fitness clearance, and headcount")
public class PersonnelController {

    private final PersonnelMovementService personnelMovementService;

    public PersonnelController(PersonnelMovementService personnelMovementService) {
        this.personnelMovementService = personnelMovementService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Register person on roster")
    public ResponseEntity<ApiResponse<PersonnelDtos.PersonDto>> createPerson(
            @Valid @RequestBody PersonnelDtos.CreatePersonRequest request) {
        PersonnelDtos.PersonDto dto = personnelMovementService.createPerson(request);
        return new ResponseEntity<>(ApiResponse.ok("Person registered on roster", dto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/assign-expedition")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Assign a person to an expedition")
    public ResponseEntity<ApiResponse<PersonnelDtos.PersonDto>> assignExpedition(
            @PathVariable UUID id,
            @Valid @RequestBody PersonnelDtos.AssignExpeditionRequest request) {
        PersonnelDtos.PersonDto dto = personnelMovementService.assignExpedition(id, request.getExpeditionId());
        return ResponseEntity.ok(ApiResponse.ok("Person assigned to expedition", dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR', 'STATION_COMMANDER')")
    @Operation(summary = "Update person movement status and location")
    public ResponseEntity<ApiResponse<PersonnelDtos.PersonDto>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PersonnelDtos.UpdatePersonStatusRequest request) {
        PersonnelDtos.PersonDto dto = personnelMovementService.updatePersonStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Movement status updated", dto));
    }

    @PatchMapping("/{id}/fitness-status")
    @PreAuthorize("hasAnyRole('HQ_ADMIN', 'LOGISTICS_COORDINATOR')")
    @Operation(summary = "Update medical/fitness clearance status")
    public ResponseEntity<ApiResponse<PersonnelDtos.PersonDto>> updateFitnessStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PersonnelDtos.UpdateFitnessStatusRequest request) {
        PersonnelDtos.PersonDto dto = personnelMovementService.updateFitnessStatus(id, request.getFitnessClearanceStatus());
        return ResponseEntity.ok(ApiResponse.ok("Fitness clearance updated to " + request.getFitnessClearanceStatus(), dto));
    }

    @GetMapping
    @Operation(summary = "Get personnel roster with optional expedition and station filters")
    public ResponseEntity<ApiResponse<List<PersonnelDtos.PersonDto>>> getRoster(
            @RequestParam(required = false) UUID expeditionId,
            @RequestParam(required = false) StationName station) {
        List<PersonnelDtos.PersonDto> roster = personnelMovementService.getRoster(expeditionId, station);
        return ResponseEntity.ok(ApiResponse.ok(roster));
    }

    @GetMapping("/roster")
    @Operation(summary = "Get personnel roster (alias)")
    public ResponseEntity<ApiResponse<List<PersonnelDtos.PersonDto>>> getRosterAlias(
            @RequestParam(required = false) UUID expeditionId,
            @RequestParam(required = false) StationName station) {
        return getRoster(expeditionId, station);
    }

    @GetMapping("/headcount")
    @Operation(summary = "Get headcount summary by station and status")
    public ResponseEntity<ApiResponse<PersonnelDtos.HeadcountSummaryDto>> getHeadcount() {
        PersonnelDtos.HeadcountSummaryDto summary = personnelMovementService.getHeadcountSummary();
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get person details by ID")
    public ResponseEntity<ApiResponse<PersonnelDtos.PersonDto>> getPersonById(@PathVariable UUID id) {
        PersonnelDtos.PersonDto dto = personnelMovementService.getPersonById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HQ_ADMIN')")
    @Operation(summary = "Delete a person from roster")
    public ResponseEntity<ApiResponse<Void>> deletePerson(@PathVariable UUID id) {
        personnelMovementService.deletePerson(id);
        return ResponseEntity.ok(ApiResponse.ok("Person removed from roster", null));
    }
}
