package com.polar.logistics.entity.enums;

public enum CargoStatus {
    PACKED,
    DISPATCHED,
    IN_TRANSIT,
    ARRIVED,
    STORED,
    CONSUMED;

    public boolean canTransitionTo(CargoStatus next) {
        if (this == next) return true;
        return switch (this) {
            case PACKED -> next == DISPATCHED;
            case DISPATCHED -> next == IN_TRANSIT;
            case IN_TRANSIT -> next == ARRIVED;
            case ARRIVED -> next == STORED;
            case STORED -> next == CONSUMED;
            case CONSUMED -> false;
        };
    }
}
