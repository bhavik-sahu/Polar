package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.entity.enums.PingSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class StatusProviderManager {

    private static final Logger log = LoggerFactory.getLogger(StatusProviderManager.class);
    private final Map<PingSource, StatusProvider> providers = new EnumMap<>(PingSource.class);

    public StatusProviderManager(List<StatusProvider> providerList) {
        for (StatusProvider provider : providerList) {
            providers.put(provider.getSupportedSource(), provider);
            log.info("Registered StatusProvider for source: {}", provider.getSupportedSource());
        }
    }

    public void dispatch(StatusPing ping) {
        PingSource source = ping.getSource() != null ? ping.getSource() : PingSource.MANUAL;
        StatusProvider provider = providers.get(source);
        if (provider != null) {
            provider.process(ping);
        } else {
            log.warn("No specific provider registered for source: {}. Using default processing.", source);
        }
    }
}
