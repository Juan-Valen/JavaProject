
package org.example.controller;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.*;
import org.example.framework.IntersectionEngine;
import org.example.framework.Event;
import org.example.model.Car;
import org.example.model.Arrival;
import org.example.model.Departure;


import org.example.view.HomeView;

import java.util.*;

public class SimulationController {
    private IntersectionEngine engine;
    private HomeView view;
    private final Map<Car, Circle> carNodes = new HashMap<>();
    private Thread simulationThread;

    private static final double ROAD_LENGTH = 290;
    private static final int STEPS_TO_NEXT_INTERSECTION = 1;

    // COORDINATES FOR CAR ANIMATION
    private static final double START_X_N = 200;
    private static final double START_Y_N = 0;
    private static final double START_X_S = 80;
    private static final double START_Y_S = 320;
    private final double START_X_E = 700;
    private static final double START_Y_E= 160;
    private static final double START_X_W = 0;
    private static final double START_Y_W = 320;

    private static final double END_X_N = 200;
    private static final double END_Y_N = 320;
    private static final double END_X_S = 80;
    private static final double END_Y_S = 0;
    private static final double END_X_E = 0;
    private static final double END_Y_E = 150;
    private static final double END_X_W = 320;
    private static final double END_Y_W = 80;

    private static final double STOP_X_N = 200;
    private static final double STOP_Y_N = 80;
    private static final double STOP_X_S = 100;
    private static final double STOP_Y_S = 80;
    private static final double STOP_X_E = 500;
    private static final double STOP_Y_E = 150;
    private static final double STOP_X_W = 80;
    private static final double STOP_Y_W = 80;


    public SimulationController(IntersectionEngine engine, HomeView view) {
        this.engine = engine;
        this.view = view;
    }


    public void startSimulation() {
        simulationThread = new Thread(() -> {
            engine.runWithCallback(this::updateView);
        });
        simulationThread.start();
    }



    private void updateView(Event event) {
        Platform.runLater(() -> {
            if (event.getType() == Event.EventType.ARRIVAL && event.getPayload() instanceof Arrival) {
                Arrival arrival = (Arrival) event.getPayload();
                Car car = arrival.car;

                arrival.getIntersection();
            }
        });
    }
    public IntersectionEngine getEngine() {
        return engine;
    }
}
