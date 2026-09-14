package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.entity.enums.PingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ManualStatusProvider implements StatusProvider {

    private static final Logger log = LoggerFactory.getLogger(ManualStatusProvider.class);

    @Override
    public PingSource getSupportedSource() {
        return PingSource.MANUAL;
    }

    @Override
    public void process(StatusPing ping) {
        log.info("Processing manual status check-in for {} ID: {}", ping.getEntityType(), ping.getEntityId());
    }
}
