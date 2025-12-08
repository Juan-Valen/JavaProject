package org.example.model;

public class QueueArrivals {
    private final Intersection intersection;
    public final boolean queueA;

    public QueueArrivals(Intersection intersection, boolean queueA) {
        this.queueA = queueA;
        this.intersection = intersection;
    }

    public  Intersection getIntersection() {
        return intersection;
    }
}
