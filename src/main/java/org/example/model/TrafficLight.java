package org.example.model;

/**
 * Represents a traffic light at an intersection.
 */
public class TrafficLight {

    /** The state/color of the traffic light. */
    public enum State  { RED, YELLOW, GREEN };

    /** The current state of the traffic light. */
    private State state;

    /** The direction the traffic light controls (e.g., "A" or "B"). */
    private String direction;

    /** Constructs a TrafficLight with the specified direction, initially set to RED.
     * @param direction The direction the traffic light controls.
     */
    public TrafficLight(String direction) {
        this.direction = direction;
        this.state = State.RED;
    }

    /**
     * Gets the current state of the traffic light.
     * @return The current state of the traffic light.
     */
    public State getState() { return state; }

    /** Sets the state of the traffic light.
     * @param state The new state of the traffic light.
     */
    public void setState(State state) {
        this.state = state;
    }
}
