package org.example.controller;


import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.framework.IntersectionEngine;
import org.example.model.TrafficLight;
import org.example.model.TrafficLightIntersection;
import org.example.view.HomeView;

import java.util.List;

public class TrafficLightController {

//    delay after green light changes to yellow
    private static int greenDelay;
//    delay after yellow light changes to red
    private static int yellowDelay;
    private HomeView homeView;
    private double cycleTime = 0.0;


    private void updateUI() {
        if (homeView != null) {
            Platform.runLater(() -> homeView.refreshTrafficLights());
        }
    }
    public void setHomeView(HomeView homeView) {
        this.homeView = homeView;
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
                updateUI();

                return greenDelay;
            }
            case YELLOW -> {
                intersection.north.setState(TrafficLight.State.RED);
                intersection.south.setState(TrafficLight.State.RED);
                intersection.east.setState(TrafficLight.State.GREEN);
                intersection.west.setState(TrafficLight.State.GREEN);
                updateUI();

                return yellowDelay;
            }
            case RED -> {
                switch (intersection.east.getState()) {
                    case GREEN -> {
                        intersection.north.setState(TrafficLight.State.RED);
                        intersection.south.setState(TrafficLight.State.RED);
                        intersection.east.setState(TrafficLight.State.YELLOW);
                        intersection.west.setState(TrafficLight.State.YELLOW);
                        updateUI();
                        return greenDelay;
                    }
                    case YELLOW -> {
                        intersection.north.setState(TrafficLight.State.GREEN);
                        intersection.south.setState(TrafficLight.State.GREEN);
                        intersection.east.setState(TrafficLight.State.RED);
                        intersection.west.setState(TrafficLight.State.RED);
                        updateUI();

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


    public void updateLightColor(Circle light, TrafficLight.State state) {
        switch (state) {
            case RED -> light.setFill(Color.RED);
            case YELLOW -> light.setFill(Color.YELLOW);
            case GREEN -> light.setFill(Color.GREEN);
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
