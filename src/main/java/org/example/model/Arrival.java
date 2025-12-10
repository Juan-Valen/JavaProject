package org.example.model;

import org.example.framework.EventList;

public final class Arrival {
    public final Car car;
    public final boolean fromA;
    public  final Intersection intersection;
    public final EventList eventList;


    public Arrival(Car car, boolean fromA, Intersection i, EventList eventList) {
        this.car = car;
        this.fromA = fromA;
        this.intersection = i;
        this.eventList = eventList;
    }

    public Intersection getIntersection() {
        return intersection;
    }
}