package org.example.controller;


import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.example.model.TrafficLight;
import org.example.view.HomeView;

import java.util.List;

public class TrafficLightController {

//    delay after green light changes to yellow
    private static int greenDelay;
//    delay after yellow light changes to red
    private static int yellowDelay;
    private Circle northLight;
    private Circle southLight;
    private Circle eastLight;
    private Circle westLight;


    private final TrafficLight north = new TrafficLight("NORTH");
    private final TrafficLight south = new TrafficLight("SOUTH");
    private final TrafficLight east = new TrafficLight("EAST");
    private final TrafficLight west = new TrafficLight("WEST");

    private double cycleTime = 0.0;

    public void refreshUI() {
        updateLightColor(northLight, north.getState());
        updateLightColor(southLight, south.getState());
        updateLightColor(eastLight, east.getState());
        updateLightColor(westLight, west.getState());
    }


    private Color toColor(TrafficLight.State state) {
        return switch(state) {
            case RED -> Color.RED;
            case YELLOW -> Color.YELLOW;
            case GREEN -> Color.GREEN;
        };
    }

    // Intersection has it's own trafficLightController
    // Checks current states and changes accordingly, returns integer used as the delay for next change. Integer defined at the top of the class.
    public int changeLights() {
        // If everything is RED, start with NS green
        if (north.getState() == TrafficLight.State.RED && east.getState() == TrafficLight.State.RED) {
            setNSGreen();
            refreshUI();
            return greenDelay;  // schedule next change
        }
        switch (north.getState()) {
            case GREEN ->  {
                north.setState(TrafficLight.State.YELLOW);
                south.setState(TrafficLight.State.YELLOW);
                east.setState(TrafficLight.State.RED);
                west.setState(TrafficLight.State.RED);
                refreshUI();
                return greenDelay;
            }
            case YELLOW -> {
                north.setState(TrafficLight.State.RED);
                south.setState(TrafficLight.State.RED);
                east.setState(TrafficLight.State.GREEN);
                west.setState(TrafficLight.State.GREEN);
                refreshUI();
                return yellowDelay;
            }
            case RED -> {
                switch (east.getState()) {
                    case GREEN -> {
                        north.setState(TrafficLight.State.RED);
                        south.setState(TrafficLight.State.RED);
                        east.setState(TrafficLight.State.YELLOW);
                        west.setState(TrafficLight.State.YELLOW);
                        refreshUI();
                        return greenDelay;
                    }
                    case YELLOW -> {
                        north.setState(TrafficLight.State.GREEN);
                        south.setState(TrafficLight.State.GREEN);
                        east.setState(TrafficLight.State.RED);
                        west.setState(TrafficLight.State.RED);
                        refreshUI();
                        return yellowDelay;
                    }
                }
            }

        }
        return 0; // should never reach here
    }

    public void setNSGreen() {
        north.setState(TrafficLight.State.GREEN);
        south.setState(TrafficLight.State.GREEN);
        east.setState(TrafficLight.State.RED);
        west.setState(TrafficLight.State.RED);
    }

    public void setEWGreen() {
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

    public void setLights(
            Circle north,
            Circle south,
            Circle east,
            Circle west
    ) {

    }


    public void setLightCircles(HomeView.LightSet lightSet) {
    }
}
