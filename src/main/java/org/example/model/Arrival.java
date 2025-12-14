package org.example.model;

/**
 * Represents the arrival of a car at an intersection.
 */
public final class Arrival {

    /** The car that is arriving. */
    public final Car car;

    /** Indicates if the car is arriving from direction A, the tracked direction, or direction B, the blocking direction.
     */
    public final boolean fromA;

    /** The intersection where the car is arriving. */
    public  final Intersection intersection;

    /** Constructs an Arrival event.
     * @param car The car that is arriving.
     * @param fromA True if the car is arriving from direction A, false if from direction B.
     * @param intersection The intersection where the car is arriving.
     */
    public Arrival(Car car, boolean fromA, Intersection intersection) {
        this.car = car;
        this.fromA = fromA;
        this.intersection = intersection;
    }

    /** Gets the intersection where the car is arriving.
     * @return The intersection.
     */
    public Intersection getIntersection() {
        return intersection;
    }
}