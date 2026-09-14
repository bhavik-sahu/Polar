package com.polar.logistics.tracking;

import com.polar.logistics.entity.EmergencyIncident;
import com.polar.logistics.entity.Person;
import com.polar.logistics.entity.enums.EmergencySeverity;
import com.polar.logistics.entity.enums.EmergencyStatus;
import com.polar.logistics.entity.enums.EmergencyTriggerType;
import com.polar.logistics.entity.enums.StationName;
import com.polar.logistics.repository.EmergencyIncidentRepository;
import com.polar.logistics.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MissedPingDetectionScheduler {

    private static final Logger log = LoggerFactory.getLogger(MissedPingDetectionScheduler.class);

    private final PersonRepository personRepository;
    private final EmergencyIncidentRepository emergencyIncidentRepository;

    @Value("${app.tracking.missed-ping-threshold-hours:24}")
    private int thresholdHours;

    public MissedPingDetectionScheduler(PersonRepository personRepository, EmergencyIncidentRepository emergencyIncidentRepository) {
        this.personRepository = personRepository;
        this.emergencyIncidentRepository = emergencyIncidentRepository;
    }

    @Scheduled(cron = "${app.tracking.missed-ping-check-cron:0 0/15 * * * ?}")
    @Transactional
    public void checkForMissedPings() {
        log.info("Running scheduled missed-ping detection job with threshold: {} hours", thresholdHours);
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(thresholdHours);

        List<Person> inactivePersonnel = personRepository.findPotentiallyInactivePersonnel(cutoffTime);

        for (Person person : inactivePersonnel) {
            // Check if active incident already exists for this person to prevent spamming
            boolean hasActiveEmergency = emergencyIncidentRepository.existsActiveIncidentForPerson(person.getId());

            if (!hasActiveEmergency) {
                StationName station = null;
                if (person.getCurrentLocation() != null) {
                    if (person.getCurrentLocation().toUpperCase().contains("MAITRI")) {
                        station = StationName.MAITRI;
                    } else if (person.getCurrentLocation().toUpperCase().contains("BHARATI")) {
                        station = StationName.BHARATI;
                    }
                }

                EmergencyIncident incident = EmergencyIncident.builder()
                        .triggerType(EmergencyTriggerType.MISSED_PING)
                        .triggeredBy(person)
                        .relatedExpedition(person.getExpedition())
                        .station(station)
                        .severity(EmergencySeverity.CRITICAL)
                        .status(EmergencyStatus.ACTIVE)
                        .lastKnownLatitude(person.getLastKnownLatitude())
                        .lastKnownLongitude(person.getLastKnownLongitude())
                        .triggeredAt(LocalDateTime.now())
                        .resolutionNotes("Automatically triggered: No status ping received from " + person.getName() + " in over " + thresholdHours + " hours.")
                        .build();

                emergencyIncidentRepository.save(incident);
                log.warn("AUTOMATED SOS CREATED: Missed ping detected for person {} (ID: {})", person.getName(), person.getId());
            }
        }
    }
}
