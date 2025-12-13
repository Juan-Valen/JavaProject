package org.example.model;

import org.example.framework.Event;
import org.example.framework.EventList;

public class TrafficLightChange {
    private final Intersection intersection;

    public TrafficLightChange(Intersection intersection) {
        this.intersection = intersection;
    }
/// ///// NOT USED
    public void run(long now, EventList eventList) {
        int delay = intersection.getTrafficLightController().changeLights();

        // Schedule next light change
        if (delay > 0) {
            eventList.add(new Event(
                    now + delay,
                    Event.EventType.LIGHT_CHANGE,
                    this,
                    "Next traffic light change"
            ));
        }

        // Check cars waiting at the light
        eventList.add(new Event(
                now,
                Event.EventType.CHECK_LIGHT,
                intersection,
                "Check cars after light change"
        ));
    }

    public Intersection getIntersection() {
        return intersection;
    }
}
