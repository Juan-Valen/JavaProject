package org.example.model;

public class TrafficLight {
    public enum State  { RED, YELLOW, GREEN };
    private State state;
    private String direction;

    public TrafficLight(String direction) {
        this.direction = direction;
        this.state = State.RED;
    }

    public State getState() { return state; }
    public String getDirection() { return direction; }

    public void setState(State state) {
        this.state = state;
    }
}
