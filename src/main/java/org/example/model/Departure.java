package org.example.model;

/**
 * Represents the departure of a car from an intersection.
 */
public final class Departure {

    /** The car that is departing. */
    public final Car car;

    /** Indicates if the car is departing from direction A, the tracked direction, or direction B, the blocking direction.
     */
    public final boolean fromA;

    /** The intersection where the car is departing. */
    public  final Intersection intersection;

    /** Constructs a Departure event.
     * @param car The car that is departing.
     * @param fromA True if the car is departing from direction A, false if from direction B.
     * @param intersection The intersection where the car is departing.
     */
    public Departure(Car car, boolean fromA, Intersection intersection) {
        this.car = car;
        this.fromA = fromA;
        this.intersection = intersection;
    }

    /** Gets the intersection where the car is departing.
     * @return The intersection.
     */
    public Intersection getIntersection() {
        return intersection;
    }
}