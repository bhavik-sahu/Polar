package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.entity.enums.PingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScheduledCheckinStatusProvider implements StatusProvider {

    private static final Logger log = LoggerFactory.getLogger(ScheduledCheckinStatusProvider.class);

    @Override
    public PingSource getSupportedSource() {
        return PingSource.SCHEDULED_CHECKIN;
    }

    @Override
    public void process(StatusPing ping) {
        log.info("Processing scheduled check-in for {} ID: {}", ping.getEntityType(), ping.getEntityId());
    }
}
