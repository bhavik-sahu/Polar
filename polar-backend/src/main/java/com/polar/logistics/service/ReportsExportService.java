package com.polar.logistics.service;

import com.polar.logistics.entity.CargoItem;
import com.polar.logistics.entity.EmergencyIncident;
import com.polar.logistics.entity.EmergencyResponseLog;
import com.polar.logistics.entity.Expedition;
import com.polar.logistics.entity.InventoryItem;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.TransitLeg;
import com.polar.logistics.entity.enums.CargoStatus;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.exception.ResourceNotFoundException;
import com.polar.logistics.repository.CargoItemRepository;
import com.polar.logistics.repository.EmergencyIncidentRepository;
import com.polar.logistics.repository.EmergencyResponseLogRepository;
import com.polar.logistics.repository.ExpeditionRepository;
import com.polar.logistics.repository.InventoryItemRepository;
import com.polar.logistics.repository.PersonRepository;
import com.polar.logistics.repository.TransitLegRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportsExportService {

    private final ExpeditionRepository expeditionRepository;
    private final TransitLegRepository transitLegRepository;
    private final PersonRepository personRepository;
    private final CargoItemRepository cargoItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final EmergencyIncidentRepository emergencyIncidentRepository;
    private final EmergencyResponseLogRepository emergencyResponseLogRepository;

    public ReportsExportService(ExpeditionRepository expeditionRepository,
                                TransitLegRepository transitLegRepository,
                                PersonRepository personRepository,
                                CargoItemRepository cargoItemRepository,
                                InventoryItemRepository inventoryItemRepository,
                                EmergencyIncidentRepository emergencyIncidentRepository,
                                EmergencyResponseLogRepository emergencyResponseLogRepository) {
        this.expeditionRepository = expeditionRepository;
        this.transitLegRepository = transitLegRepository;
        this.personRepository = personRepository;
        this.cargoItemRepository = cargoItemRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.emergencyIncidentRepository = emergencyIncidentRepository;
        this.emergencyResponseLogRepository = emergencyResponseLogRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getExpeditionSummaryReport(UUID expeditionId) {
        Expedition expedition = expeditionRepository.findById(expeditionId)
                .orElseThrow(() -> new ResourceNotFoundException("Expedition not found with ID: " + expeditionId));

        List<TransitLeg> legs = transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(expeditionId);
        List<Person> personnel = personRepository.findByExpeditionId(expeditionId);
        List<CargoItem> cargo = cargoItemRepository.findByExpeditionId(expeditionId);

        double totalCargoWeight = cargo.stream()
                .filter(c -> c.getWeightKg() != null)
                .mapToDouble(c -> c.getWeightKg().doubleValue())
                .sum();

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "EXPEDITION_SUMMARY");
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("expeditionId", expedition.getId());
        report.put("name", expedition.getName());
        report.put("objective", expedition.getObjective());
        report.put("startDate", expedition.getStartDate());
        report.put("endDate", expedition.getEndDate());
        report.put("status", expedition.getStatus());
        report.put("transitLegsCount", legs.size());
        report.put("transitLegs", legs);
        report.put("personnelCount", personnel.size());
        report.put("personnelRoster", personnel);
        report.put("cargoCount", cargo.size());
        report.put("totalCargoWeightKg", totalCargoWeight);
        report.put("cargoManifest", cargo);

        return report;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllExpeditionsReport() {
        List<Expedition> expeditions = expeditionRepository.findAll();
        List<Map<String, Object>> summaries = new ArrayList<>();

        for (Expedition exp : expeditions) {
            List<TransitLeg> legs = transitLegRepository.findByExpeditionIdOrderBySequenceOrderAsc(exp.getId());
            List<Person> personnel = personRepository.findByExpeditionId(exp.getId());
            List<CargoItem> cargo = cargoItemRepository.findByExpeditionId(exp.getId());

            double totalCargoWeight = cargo.stream()
                    .filter(c -> c.getWeightKg() != null)
                    .mapToDouble(c -> c.getWeightKg().doubleValue())
                    .sum();

            Map<String, Object> item = new HashMap<>();
            item.put("id", exp.getId());
            item.put("name", exp.getName());
            item.put("objective", exp.getObjective());
            item.put("startDate", exp.getStartDate());
            item.put("endDate", exp.getEndDate());
            item.put("status", exp.getStatus());
            item.put("transitLegsCount", legs.size());
            item.put("personnelCount", personnel.size());
            item.put("cargoCount", cargo.size());
            item.put("totalCargoWeightKg", totalCargoWeight);
            summaries.add(item);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "ALL_EXPEDITIONS_SUMMARY");
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("totalExpeditions", expeditions.size());
        report.put("expeditions", summaries);
        return report;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCargoManifestReport(String station, String status) {
        List<CargoItem> cargo = cargoItemRepository.findAll();

        if (status != null && !status.equalsIgnoreCase("ALL") && !status.isBlank()) {
            try {
                CargoStatus cStatus = CargoStatus.valueOf(status.toUpperCase());
                cargo = cargo.stream().filter(c -> c.getStatus() == cStatus).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }

        if (station != null && !station.equalsIgnoreCase("ALL") && !station.equalsIgnoreCase("GLOBAL") && !station.isBlank()) {
            cargo = cargo.stream().filter(c -> {
                if (c.getCurrentStationLocation() != null && c.getCurrentStationLocation().name().equalsIgnoreCase(station)) return true;
                if (c.getName() != null && c.getName().toLowerCase().contains(station.toLowerCase())) return true;
                return false;
            }).collect(Collectors.toList());
        }

        double totalWeight = cargo.stream()
                .filter(c -> c.getWeightKg() != null)
                .mapToDouble(c -> c.getWeightKg().doubleValue())
                .sum();

        long hazardousCount = cargo.stream()
                .filter(c -> c.getCategory() != null && c.getCategory().name().equalsIgnoreCase("HAZARDOUS"))
                .count();

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "CARGO_MANIFEST");
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("totalItems", cargo.size());
        report.put("totalWeightKg", totalWeight);
        report.put("hazardousItemsCount", hazardousCount);
        report.put("filterStation", station);
        report.put("filterStatus", status);
        report.put("cargoItems", cargo);
        return report;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInventorySnapshotReport(String station) {
        List<InventoryItem> items;
        if (station != null && !station.equalsIgnoreCase("ALL") && !station.equalsIgnoreCase("GLOBAL") && !station.isBlank()) {
            try {
                StationName st = StationName.valueOf(station.toUpperCase());
                items = inventoryItemRepository.findByStation(st);
            } catch (IllegalArgumentException e) {
                items = inventoryItemRepository.findAll();
            }
        } else {
            items = inventoryItemRepository.findAll();
        }

        long lowStockCount = items.stream()
                .filter(i -> i.getQuantity() <= i.getReorderThreshold())
                .count();

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "INVENTORY_SNAPSHOT");
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("totalItems", items.size());
        report.put("lowStockCount", lowStockCount);
        report.put("filterStation", station);
        report.put("items", items);
        return report;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPersonnelRosterReport(String station, UUID expeditionId) {
        List<Person> personnel;
        if (expeditionId != null) {
            personnel = personRepository.findByExpeditionId(expeditionId);
        } else {
            personnel = personRepository.findAll();
        }

        if (station != null && !station.equalsIgnoreCase("ALL") && !station.equalsIgnoreCase("GLOBAL") && !station.isBlank()) {
            personnel = personnel.stream().filter(p ->
                (p.getCurrentLocation() != null && p.getCurrentLocation().toLowerCase().contains(station.toLowerCase())) ||
                (p.getCurrentStatus() != null && p.getCurrentStatus().name().equalsIgnoreCase(station))
            ).collect(Collectors.toList());
        }

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "PERSONNEL_ROSTER");
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("totalPersonnel", personnel.size());
        report.put("filterStation", station);
        report.put("filterExpeditionId", expeditionId);
        report.put("personnel", personnel);
        return report;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOperationalAuditReport() {
        List<EmergencyIncident> emergencies = emergencyIncidentRepository.findAllByOrderByTriggeredAtDesc();
        List<EmergencyResponseLog> logs = new ArrayList<>();
        for (EmergencyIncident em : emergencies) {
            logs.addAll(emergencyResponseLogRepository.findByIncidentIdOrderByTimestampDesc(em.getId()));
        }

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", "AUDIT_LOGS");
        report.put("generatedAt", LocalDateTime.now().toString());
        report.put("totalEmergencies", emergencies.size());
        report.put("totalResponseLogs", logs.size());
        report.put("emergencies", emergencies);
        report.put("responseLogs", logs);
        return report;
    }
}
