package org.example.model;

public class TrafficLight {
    public enum State  { RED, YELLOW, GREEN };
    private State state;
    private String location;
    private double timeInState = 0.0; // duration in state


    public TrafficLight(String location) {
        this.location = location;
        this.state = State.RED; // default
    }


    public State getState() { return state; }
    public String getLocation() { return location; }

    public void setState(State state) {
        this.state = state;
    }
}
