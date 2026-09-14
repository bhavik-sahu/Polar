package com.polar.logistics.tracking;

import com.polar.logistics.document.StatusPing;
import com.polar.logistics.entity.enums.PingSource;

public interface StatusProvider {
    PingSource getSupportedSource();
    void process(StatusPing ping);
}
