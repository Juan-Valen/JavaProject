package org.example.model;

public final class Arrival {
    public final Car car;
    public final boolean fromA;

    public Arrival(Car car, boolean fromA) {
        this.car = car;
        this.fromA = fromA;
    }
}