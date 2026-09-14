package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.entity.enums.PingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SystemStatusProvider implements StatusProvider {

    private static final Logger log = LoggerFactory.getLogger(SystemStatusProvider.class);

    @Override
    public PingSource getSupportedSource() {
        return PingSource.SYSTEM;
    }

    @Override
    public void process(StatusPing ping) {
        log.info("Processing automated system telemetry for {} ID: {}", ping.getEntityType(), ping.getEntityId());
    }
}
