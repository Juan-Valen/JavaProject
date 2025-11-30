
package org.example.framework;


import org.example.model.TrafficLight;

public class TrafficLightEvent {
    private final long timestamp;
    private final String direction;
    private final TrafficLight.State newState;

    public TrafficLightEvent(String direction, TrafficLight.State newState) {
        this.timestamp = System.currentTimeMillis();
        this.direction = direction;
        this.newState = newState;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + direction + " light changed to " + newState;
    }
}


