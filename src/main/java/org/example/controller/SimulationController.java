
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


import org.example.model.Intersection;
import org.example.view.HomeView;

import java.util.*;

public class SimulationController {
    private IntersectionEngine engine;
    private HomeView view;
    private final Map<Car, Circle> carNodes = new HashMap<>();


    // COORDINATES FOR CAR ANIMATION
    private static final double START_X_N = 300;
    private static final double START_Y_N = 0;
    private static final double START_X_S = 380;
    private static final double START_Y_S = 700;
    private static final double START_X_E = 700;
    private static final double START_Y_E= 300;
    private static final double START_X_W = 0;
    private static final double START_Y_W = 380;


    private static final double END_X_N = 300;
    private static final double END_Y_N = 700;
    private static final double END_X_S = 380;
    private static final double END_Y_S = 0;
    private static final double END_X_E = 0;
    private static final double END_Y_E = 300;
    private static final double END_X_W = 700;
    private static final double END_Y_W = 380;


    private static final double STOP_X_N = 300;
    private static final double STOP_Y_N = 260;
    private static final double STOP_X_S = 380;
    private static final double STOP_Y_S = 380;
    private static final double STOP_X_E = 440;
    private static final double STOP_Y_E = 300;
    private static final double STOP_X_W = 260;
    private static final double STOP_Y_W = 380;




    private final TrafficLightController trafficLightController;


    public SimulationController(IntersectionEngine engine, HomeView view) {
        this.engine = engine;
        this.view = view;
        this.trafficLightController = engine.getTrafficLightController();
    }

    public void startSimulation() {
        // Run the engine in a separate thread so UI stays responsive
        new Thread(() -> {
            engine.runWithCallback(this::updateView);
        }).start();
    }


    private void updateView(Event event) {
        Platform.runLater(() -> {
            view.updateLights(engine.getTrafficLightController());
            view.updateQueues(engine.getIntersection1().getQueueStates());

            if (event.getType() == Event.EventType.ARRIVAL && event.getPayload() instanceof Arrival) {
                Arrival arrival = (Arrival) event.getPayload();
                Car car = arrival.car;

                // Create car node
                Circle carShape = new Circle(10, arrival.fromA ? Color.BLUE : Color.RED);
                double startX = arrival.fromA ? START_X_N : START_X_E;
                double startY = arrival.fromA ? START_Y_N : START_Y_E;
                double stopX = arrival.fromA ? STOP_X_N : STOP_X_E;
                double stopY = arrival.fromA ? STOP_Y_N : STOP_Y_E;

                carShape.setCenterX(startX);
                carShape.setCenterY(startY);
                view.addCarNode(carShape);
                carNodes.put(car, carShape);

                // Animate to stop position
                TranslateTransition moveToStop = new TranslateTransition(Duration.millis(1000), carShape);
                moveToStop.setToX(stopX - startX);
                moveToStop.setToY(stopY - startY);
                moveToStop.play();
            }

            if (event.getType() == Event.EventType.DEPARTURE && event.getPayload() instanceof Departure) {
                Departure departure = (Departure) event.getPayload();
                Car car = departure.car;

                Circle carShape = carNodes.get(car);
                if (carShape != null) {
                    double endX = departure.fromA ? END_X_N : END_X_E;
                    double endY = departure.fromA ? END_Y_N : END_Y_E;

                    TranslateTransition moveToEnd = new TranslateTransition(Duration.millis(1000), carShape);
                    moveToEnd.setToX(endX - carShape.getCenterX());
                    moveToEnd.setToY(endY - carShape.getCenterY());
                    moveToEnd.play();
                }
            }
        });
    }


    private void animateQueue(List<Car> queue, double startX, double startY, double endX, double endY) {
        for (Car car : queue) {
            Circle carShape = new Circle(10, Color.BLUE);
            carShape.setCenterX(startX);
            carShape.setCenterY(startY);
            view.addCarNode(carShape);

            TranslateTransition move = new TranslateTransition(Duration.millis(1000), carShape);
            move.setToX(endX - startX);
            move.setToY(endY - startY);
            move.play();
        }
    }
}



