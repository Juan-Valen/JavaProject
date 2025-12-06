package org.example.model;

public final class Departure {
    public final Car car;
    public final boolean fromA;
    public  final Intersection intersection;

    public Departure(Car car, boolean fromA, Intersection intersection) {
        this.car = car;
        this.fromA = fromA;
        this.intersection = intersection;
    }

    public Intersection getIntersection() {
        return intersection;
    }
}