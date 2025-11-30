package org.example.controller;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.model.TrafficLight;
import java.util.List;

public class TrafficLightController {
    private final TrafficLight north = new TrafficLight("NORTH");
    private final TrafficLight south = new TrafficLight("SOUTH");
    private final TrafficLight east = new TrafficLight("EAST");
    private final TrafficLight west = new TrafficLight("WEST");

    private double cycleTime = 0.0;

    public void update(double seconds) {
        cycleTime += seconds;

        double greenDuration = 4;   // GREEN for 5s
        double yellowDuration = 2;  // YELLOW for 2s
        double phaseDuration = greenDuration + yellowDuration; // 7s per phase

        if (cycleTime < greenDuration) {
            // Phase 1: NS GREEN, EW RED
            north.setState(TrafficLight.State.GREEN);
            south.setState(TrafficLight.State.GREEN);
            east.setState(TrafficLight.State.RED);
            west.setState(TrafficLight.State.RED);
        } else if (cycleTime < phaseDuration) {
            // Phase 1 YELLOW: NS YELLOW, EW RED
            north.setState(TrafficLight.State.YELLOW);
            south.setState(TrafficLight.State.YELLOW);
            east.setState(TrafficLight.State.RED);
            west.setState(TrafficLight.State.RED);
        } else if (cycleTime < phaseDuration + greenDuration) {
            // Phase 2: EW GREEN, NS RED
            north.setState(TrafficLight.State.RED);
            south.setState(TrafficLight.State.RED);
            east.setState(TrafficLight.State.GREEN);
            west.setState(TrafficLight.State.GREEN);
        } else if (cycleTime < phaseDuration * 2) {
            // Phase 2 YELLOW: EW YELLOW, NS RED
            north.setState(TrafficLight.State.RED);
            south.setState(TrafficLight.State.RED);
            east.setState(TrafficLight.State.YELLOW);
            west.setState(TrafficLight.State.YELLOW);
        } else {
            cycleTime = 0; // restart cycle
        }
    }




    private void setNSGreen() {
        north.setState(TrafficLight.State.GREEN);
        south.setState(TrafficLight.State.GREEN);
        east.setState(TrafficLight.State.RED);
        west.setState(TrafficLight.State.RED);
    }

    private void setEWGreen() {
        north.setState(TrafficLight.State.RED);
        south.setState(TrafficLight.State.RED);
        east.setState(TrafficLight.State.GREEN);
        west.setState(TrafficLight.State.GREEN);
    }

    public List<TrafficLight> getLights() {
        return List.of(north, south, east, west);
    }

    public void updateLightColor(Circle light, TrafficLight.State state) {
        switch (state) {
            case RED -> light.setFill(Color.RED);
            case YELLOW -> light.setFill(Color.YELLOW);
            case GREEN -> light.setFill(Color.GREEN);
        }
    }

    public TrafficLight getNorth() { return north; }
    public TrafficLight getSouth() { return south; }
    public TrafficLight getEast()  { return east; }
    public TrafficLight getWest()  { return west; }

    public TrafficLight.State getNorthState() { return north.getState(); }
    public TrafficLight.State getSouthState() { return south.getState(); }
    public TrafficLight.State getEastState()  { return east.getState();  }
    public TrafficLight.State getWestState()  { return west.getState();  }


}
