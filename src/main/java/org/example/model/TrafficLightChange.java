package org.example.model;

/**
 * Represents the event of a traffic light changing at an intersection.
 */
public final class TrafficLightChange {

    /** The intersection where the traffic light is changing. */
    private Intersection intersection;

    /** Constructs a TrafficLightChange event.
     * @param intersection The intersection where the traffic light is changing.
     */
    public TrafficLightChange(Intersection intersection) {
        this.intersection = intersection;


    }

    /** Gets the intersection where the traffic light is changing.
     * @return The intersection.
     */
    public Intersection getIntersection() {
        return intersection;
    }

}
