package org.example.controller;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.framework.IntersectionEngine;
import org.example.model.TrafficLight;
import org.example.model.TrafficLightIntersection;

import java.util.List;

public class TrafficLightController {

    IntersectionEngine intersectionEngine;

//    delay after green light changes to yellow
    private static int greenDelay;
//    delay after yellow light changes to red
    private static int yellowDelay;


    private final TrafficLight north = new TrafficLight("NORTH");
    private final TrafficLight south = new TrafficLight("SOUTH");
    private final TrafficLight east = new TrafficLight("EAST");
    private final TrafficLight west = new TrafficLight("WEST");

    private double cycleTime = 0.0;

    public void update(double seconds) {
        cycleTime += seconds;

        double greenDuration = 1;
        double yellowDuration = 0.5;
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

    // Add specific intersection control later
    // Checks current states and changes accordingly, returns integer used as the delay for next change. Integer defined at the top of the class.
    public int changeLights(TrafficLightIntersection intersection) {

        switch (intersection.north.getState()) {
            case GREEN ->  {
                intersection.north.setState(TrafficLight.State.YELLOW);
                intersection.south.setState(TrafficLight.State.YELLOW);
                intersection.east.setState(TrafficLight.State.RED);
                intersection.west.setState(TrafficLight.State.RED);

                return greenDelay;
            }
            case YELLOW -> {
                intersection.north.setState(TrafficLight.State.RED);
                intersection.south.setState(TrafficLight.State.RED);
                intersection.east.setState(TrafficLight.State.GREEN);
                intersection.west.setState(TrafficLight.State.GREEN);

                return yellowDelay;
            }
            case RED -> {
                switch (intersection.east.getState()) {
                    case GREEN -> {
                        intersection.north.setState(TrafficLight.State.RED);
                        intersection.south.setState(TrafficLight.State.RED);
                        intersection.east.setState(TrafficLight.State.YELLOW);
                        intersection.west.setState(TrafficLight.State.YELLOW);

                        return greenDelay;
                    }
                    case YELLOW -> {
                        intersection.north.setState(TrafficLight.State.GREEN);
                        intersection.south.setState(TrafficLight.State.GREEN);
                        intersection.east.setState(TrafficLight.State.RED);
                        intersection.west.setState(TrafficLight.State.RED);

                        return yellowDelay;
                    }
                }
            }

        }
        return 0; // should never reach here
    }

    public void setNSGreen(TrafficLightIntersection intersection) {
        intersection.north.setState(TrafficLight.State.GREEN);
        intersection.south.setState(TrafficLight.State.GREEN);
        intersection.east.setState(TrafficLight.State.RED);
        intersection.west.setState(TrafficLight.State.RED);
    }

    public void setEWGreen(TrafficLightIntersection intersection) {
        intersection.north.setState(TrafficLight.State.RED);
        intersection.south.setState(TrafficLight.State.RED);
        intersection.east.setState(TrafficLight.State.GREEN);
        intersection.west.setState(TrafficLight.State.GREEN);
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


    public boolean isGreen(String direction) {
        switch (direction.toUpperCase()) {
            case "NORTH":
                return getNorthState() == TrafficLight.State.GREEN;
            case "SOUTH":
                return getSouthState() == TrafficLight.State.GREEN;
            case "EAST":
                return getEastState() == TrafficLight.State.GREEN;
            case "WEST":
                return getWestState() == TrafficLight.State.GREEN;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    public int getGreenDelay() {
        return greenDelay;
    }

    public void setGreenDelay(int greenDelay) {
        TrafficLightController.greenDelay = greenDelay;
    }

    public int getYellowDelay() {
        return yellowDelay;
    }

    public void setYellowDelay(int yellowDelay) {
        TrafficLightController.yellowDelay = yellowDelay;
    }


}
