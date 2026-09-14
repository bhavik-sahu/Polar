package com.polar.logistics.config;

import com.polar.logistics.entity.*;
import com.polar.logistics.entity.enums.*;
import com.polar.logistics.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ExpeditionRepository expeditionRepository;
    private final TransitLegRepository transitLegRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final CargoItemRepository cargoItemRepository;
    private final PersonRepository personRepository;
    private final EmergencyIncidentRepository emergencyIncidentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ExpeditionRepository expeditionRepository,
                      TransitLegRepository transitLegRepository,
                      InventoryItemRepository inventoryItemRepository,
                      CargoItemRepository cargoItemRepository,
                      PersonRepository personRepository,
                      EmergencyIncidentRepository emergencyIncidentRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.expeditionRepository = expeditionRepository;
        this.transitLegRepository = transitLegRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.cargoItemRepository = cargoItemRepository;
        this.personRepository = personRepository;
        this.emergencyIncidentRepository = emergencyIncidentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded with initial data. Skipping seeding.");
            return;
        }

        log.info("Seeding initial verified and simulated datasets...");

        // 1. Seed Users
        User admin = userRepository.save(User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .role(UserRole.HQ_ADMIN)
                .build());

        User coordinator = userRepository.save(User.builder()
                .username("coordinator")
                .passwordHash(passwordEncoder.encode("Coord@123"))
                .role(UserRole.LOGISTICS_COORDINATOR)
                .build());

        User cmdMaitri = userRepository.save(User.builder()
                .username("commander_maitri")
                .passwordHash(passwordEncoder.encode("Cmd@123"))
                .role(UserRole.STATION_COMMANDER)
                .station(StationName.MAITRI)
                .build());

        User cmdBharati = userRepository.save(User.builder()
                .username("commander_bharati")
                .passwordHash(passwordEncoder.encode("Cmd@123"))
                .role(UserRole.STATION_COMMANDER)
                .station(StationName.BHARATI)
                .build());

        User member = userRepository.save(User.builder()
                .username("member")
                .passwordHash(passwordEncoder.encode("User@123"))
                .role(UserRole.EXPEDITION_MEMBER)
                .build());

        // 2. Seed Expeditions & Transit Legs
        // Verified 45-ISEA
        Expedition exp45 = expeditionRepository.save(Expedition.builder()
                .name("45-ISEA Maitri/Bharati Expedition")
                .objective("45th Indian Scientific Expedition to Antarctica summer and winter component")
                .startDate(LocalDate.of(2025, 10, 31))
                .endDate(LocalDate.of(2026, 5, 15))
                .status(ExpeditionStatus.COMPLETED)
                .createdBy(admin)
                .build());

        transitLegRepository.saveAll(List.of(
                TransitLeg.builder()
                        .expedition(exp45)
                        .sequenceOrder(1)
                        .origin("Mumbai Port")
                        .destination("Cape Town, South Africa")
                        .mode(TransitMode.SHIP)
                        .expectedDeparture(LocalDateTime.of(2025, 10, 31, 10, 0))
                        .expectedArrival(LocalDateTime.of(2025, 11, 20, 18, 0))
                        .actualDeparture(LocalDateTime.of(2025, 10, 31, 11, 30))
                        .actualArrival(LocalDateTime.of(2025, 11, 20, 17, 45))
                        .build(),
                TransitLeg.builder()
                        .expedition(exp45)
                        .sequenceOrder(2)
                        .origin("Cape Town, South Africa")
                        .destination("Maitri Station (via Lazarev Sea)")
                        .mode(TransitMode.SHIP)
                        .expectedDeparture(LocalDateTime.of(2025, 11, 25, 8, 0))
                        .expectedArrival(LocalDateTime.of(2025, 12, 10, 14, 0))
                        .actualDeparture(LocalDateTime.of(2025, 11, 25, 8, 0))
                        .actualArrival(LocalDateTime.of(2025, 12, 9, 20, 0))
                        .build()
        ));

        // Verified 46-ISEA (Planning / Upcoming)
        Expedition exp46 = expeditionRepository.save(Expedition.builder()
                .name("46-ISEA Maitri Summer Contingent")
                .objective("46th Indian Scientific Expedition summer research and station maintenance")
                .startDate(LocalDate.of(2026, 11, 15))
                .endDate(LocalDate.of(2027, 5, 15))
                .status(ExpeditionStatus.PLANNED)
                .createdBy(coordinator)
                .build());

        TransitLeg leg46_1 = transitLegRepository.save(TransitLeg.builder()
                .expedition(exp46)
                .sequenceOrder(1)
                .origin("Goa (NCPOR HQ) / Mumbai")
                .destination("Cape Town Staging Port")
                .mode(TransitMode.FLIGHT)
                .expectedDeparture(LocalDateTime.of(2026, 11, 1, 9, 0))
                .expectedArrival(LocalDateTime.of(2026, 11, 5, 16, 0))
                .build());

        TransitLeg leg46_2 = transitLegRepository.save(TransitLeg.builder()
                .expedition(exp46)
                .sequenceOrder(2)
                .origin("Cape Town Staging Port")
                .destination("Maitri Station, Schirmacher Oasis")
                .mode(TransitMode.SHIP)
                .expectedDeparture(LocalDateTime.of(2026, 11, 15, 6, 0))
                .expectedArrival(LocalDateTime.of(2026, 11, 28, 18, 0))
                .build());

        // 3. Seed Inventory Items
        InventoryItem maitriDiesel = inventoryItemRepository.save(InventoryItem.builder()
                .name("Diesel Fuel (Polar Grade)")
                .category("FUEL")
                .station(StationName.MAITRI)
                .quantity(4200)
                .unit("Litres")
                .reorderThreshold(3000)
                .dailyConsumption(45.0)
                .lastUpdated(LocalDateTime.now())
                .build());

        InventoryItem maitriRations = inventoryItemRepository.save(InventoryItem.builder()
                .name("Dry Rations & Preserved Food")
                .category("FOOD")
                .station(StationName.MAITRI)
                .quantity(1800)
                .unit("kg")
                .reorderThreshold(2000)
                .dailyConsumption(28.0)
                .expiryDate(LocalDate.now().plusMonths(6))
                .lastUpdated(LocalDateTime.now())
                .build());

        InventoryItem bharatiDiesel = inventoryItemRepository.save(InventoryItem.builder()
                .name("Diesel Fuel (Polar Grade)")
                .category("FUEL")
                .station(StationName.BHARATI)
                .quantity(5100)
                .unit("Litres")
                .reorderThreshold(3000)
                .dailyConsumption(40.0)
                .lastUpdated(LocalDateTime.now())
                .build());

        InventoryItem bharatiOxygen = inventoryItemRepository.save(InventoryItem.builder()
                .name("Medical Oxygen Cylinders")
                .category("MEDICAL")
                .station(StationName.BHARATI)
                .quantity(100)
                .unit("Cylinders")
                .reorderThreshold(100)
                .dailyConsumption(5.0)
                .expiryDate(LocalDate.now().plusDays(25))
                .lastUpdated(LocalDateTime.now())
                .build());

        // 4. Seed Personnel
        Person p1 = personRepository.save(Person.builder()
                .name("Dr. S. K. Raman")
                .role(PersonRole.LEADER)
                .fitnessClearanceStatus(FitnessClearanceStatus.CLEARED)
                .currentStatus(PersonStatus.AT_STATION)
                .currentLocation("Maitri Station")
                .expedition(exp45)
                .lastKnownLatitude(-70.767)
                .lastKnownLongitude(11.733)
                .lastPingTime(LocalDateTime.now().minusHours(2))
                .build());

        Person p2 = personRepository.save(Person.builder()
                .name("Er. Vikram Sengupta")
                .role(PersonRole.LOGISTICS_STAFF)
                .fitnessClearanceStatus(FitnessClearanceStatus.CLEARED)
                .currentStatus(PersonStatus.IN_TRANSIT)
                .currentLocation("MV Vasiliy Golovnin (Transit Cape Town to Maitri)")
                .expedition(exp46)
                .currentTransitLeg(leg46_2)
                .lastKnownLatitude(-55.200)
                .lastKnownLongitude(15.400)
                .lastPingTime(LocalDateTime.now().minusHours(4))
                .build());

        Person p3 = personRepository.save(Person.builder()
                .name("Dr. Ananya Iyer")
                .role(PersonRole.SCIENTIST)
                .fitnessClearanceStatus(FitnessClearanceStatus.CLEARED)
                .currentStatus(PersonStatus.AT_STATION)
                .currentLocation("Bharati Station, Larsemann Hills")
                .lastKnownLatitude(-69.407)
                .lastKnownLongitude(76.191)
                .lastPingTime(LocalDateTime.now().minusHours(1))
                .build());

        Person p4 = personRepository.save(Person.builder()
                .name("Anil Verma")
                .role(PersonRole.SUPPORT)
                .fitnessClearanceStatus(FitnessClearanceStatus.PENDING)
                .currentStatus(PersonStatus.IN_INDIA)
                .currentLocation("NCPOR Goa HQ")
                .expedition(exp46)
                .build());

        // 5. Seed Cargo Items
        cargoItemRepository.save(CargoItem.builder()
                .name("Direct Air Cargo - Medical Supplies & Provisions")
                .category(CargoCategory.GENERAL)
                .weightKg(new BigDecimal("18000.00"))
                .status(CargoStatus.STORED)
                .expedition(exp45)
                .currentStationLocation(StationLocation.MAITRI)
                .linkedInventoryItem(maitriRations)
                .build());

        cargoItemRepository.save(CargoItem.builder()
                .name("Lambert Glacier Ice Core Drill Assembly")
                .category(CargoCategory.SCIENTIFIC)
                .weightKg(new BigDecimal("640.00"))
                .status(CargoStatus.IN_TRANSIT)
                .expedition(exp46)
                .currentTransitLeg(leg46_1)
                .currentStationLocation(StationLocation.IN_TRANSIT)
                .lastKnownLatitude(-33.924)
                .lastKnownLongitude(18.424)
                .lastPingTime(LocalDateTime.now().minusHours(5))
                .build());

        cargoItemRepository.save(CargoItem.builder()
                .name("Backup Diesel Generator 50kVA")
                .category(CargoCategory.GENERAL)
                .weightKg(new BigDecimal("1250.00"))
                .status(CargoStatus.PACKED)
                .expedition(exp46)
                .currentStationLocation(StationLocation.INDIA)
                .build());

        // 6. Seed Active Emergency Incident
        emergencyIncidentRepository.save(EmergencyIncident.builder()
                .triggerType(EmergencyTriggerType.MANUAL_SOS)
                .triggeredBy(p3)
                .station(StationName.BHARATI)
                .severity(EmergencySeverity.MEDIUM)
                .status(EmergencyStatus.ACTIVE)
                .lastKnownLatitude(-69.407)
                .lastKnownLongitude(76.191)
                .triggeredAt(LocalDateTime.now().minusHours(3))
                .resolutionNotes("Generator #2 intermittent cooling pressure drop. Secondary generator active; inspection ongoing.")
                .build());

        log.info("Data seeding completed successfully. Pre-seeded users: admin, coordinator, commander_maitri, commander_bharati, member.");
    }
}
