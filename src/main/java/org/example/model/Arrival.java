package org.example.model;

public final class Arrival {
    public final Car car;
    public final boolean fromA;
    public  final Intersection intersection;

    public Arrival(Car car, boolean fromA, Intersection intersection) {
        this.car = car;
        this.fromA = fromA;
        this.intersection = intersection;
    }

    public Intersection getIntersection() {
        return intersection;
    }
}