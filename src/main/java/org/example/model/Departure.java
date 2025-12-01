package org.example.model;

public final class Departure {
    public final Car car;
    public final boolean fromA;

    public Departure(Car car, boolean fromA) {
        this.car = car;
        this.fromA = fromA;
    }
}