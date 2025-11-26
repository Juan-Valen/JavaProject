package org.example.model;

import org.example.framework.IEventType;

public enum EventType implements IEventType {
    ARRIVAL,
    EXIT,
    DEPART,
    TRAFFIC_TOGGLE,
    CHECK_TRAFFIC
}
