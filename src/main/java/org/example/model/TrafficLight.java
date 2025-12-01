package org.example.model;

public class TrafficLight {
    public enum State  { RED, YELLOW, GREEN };
    private State state;
    private String direction;
    private double timeInState = 0.0; // duration in state


    public TrafficLight(String location) {
        this.direction = direction;
        this.state = State.RED;
    }


    public State getState() { return state; }
    public String getDirection() { return direction; }

    public void setState(State state) {
        this.state = state;
    }
}
