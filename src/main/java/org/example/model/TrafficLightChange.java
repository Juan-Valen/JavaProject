package org.example.model;


public final class TrafficLightChange {
    private Intersection intersection;

    public TrafficLightChange(Intersection intersection) {
        this.intersection = intersection;


    }

    public Intersection getIntersection() {
        return intersection;
    }

}
