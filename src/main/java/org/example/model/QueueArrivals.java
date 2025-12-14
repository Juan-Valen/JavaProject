package org.example.model;

/**
 * Represents the event of cars arriving at a queue in an intersection.
 * To the first intersection in direcction A and all intersections in direction B.
 */
public class QueueArrivals {

    /** The intersection where the cars are arriving. */
    private final Intersection intersection;

    /** Indicates if the cars are arriving at queue A (true) or queue B (false). */
    public final boolean queueA;

    /** Constructs a QueueArrivals event.
     * @param intersection The intersection where the cars are arriving.
     * @param queueA True if the cars are arriving at queue A, false if at queue B.
     */
    public QueueArrivals(Intersection intersection, boolean queueA) {
        this.queueA = queueA;
        this.intersection = intersection;
    }

    /** Gets the intersection where the cars are arriving.
     * @return The intersection.
     */
    public  Intersection getIntersection() {
        return intersection;
    }
}
