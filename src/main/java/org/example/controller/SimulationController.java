
package org.example.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
    private Thread simulationThread;

    private static final double ROAD_LENGTH = 280;


    public SimulationController(IntersectionEngine engine, HomeView view) {
        this.engine = engine;
        this.view = view;
    }


    public void startSimulation() {
        simulationThread = new Thread(() -> {
            engine.runWithCallback(this::updateView);
        });
        simulationThread.start();
        // checks the status of the lights every 50ms
        Timeline lightRefresh = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            view.refreshTrafficLights();
        }));
        lightRefresh.setCycleCount(Animation.INDEFINITE);
        lightRefresh.play();
    }



    private void updateView(Event event) {

            if (event.getType() == Event.EventType.ARRIVAL && event.getPayload() instanceof Arrival) {
                {
                    Arrival arrival = (Arrival) event.getPayload();
                    Intersection intersection = arrival.getIntersection();
                    Circle carNode = new Circle(7, arrival.fromA ? Color.BLUE : Color.BLACK);
                    TranslateTransition move = new TranslateTransition(Duration.millis(300), carNode);
                    view.addCarNode(carNode); // Add to UI
                    carNodes.put(arrival.car, carNode); // Add to list for departure

                    // ARRIVAL FROM A -> tracked cars -> horizontal movement
                    if (arrival.fromA) {
                        double startY = 200;
                        // Get intersection index (0-3)
//                        int intersectionIdx = Integer.parseInt(name.split("-")[1]);
                        int intersectionIdx = engine.getIntersectionList().indexOf(intersection);
                        double startX = -170 + (intersectionIdx) * ROAD_LENGTH;
                        carNode.setLayoutX(startX);
                        carNode.setLayoutY(startY);
                        // Reset any previous translation
                        carNode.setTranslateX(0);
                        carNode.setTranslateY(0);
                        // Animate one road segment
                        move.setByX(ROAD_LENGTH);
                        move.play()
                        ;
                    }
                    // ARRIVAL not from A -> untracked cars -> vertical movement
                    else {
                        double startY = -170;
                        // Get intersection index (0-3)
//                        int intersectionIdx = Integer.parseInt(name.split("-")[1]);
                        int intersectionIdx = engine.getIntersectionList().indexOf(intersection);
                        double startX = 160 + (intersectionIdx) * ROAD_LENGTH;
                        carNode.setLayoutX(startX);
                        carNode.setLayoutY(startY);
                        // Reset any previous translation
                        carNode.setTranslateX(0);
                        carNode.setTranslateY(0);
                        // Animate one road segment
                        move.setByY(ROAD_LENGTH);
                        move.play();
                    }
                }

            }

            else if (event.getType() == Event.EventType.DEPARTURE && event.getPayload() instanceof Departure) {
                Departure departure = (Departure) event.getPayload();
                Car car = departure.car;
                Intersection intersection = departure.getIntersection();

                // fetch existing node
                Circle carNode = carNodes.get(car);
                if (carNode == null) {
                    System.out.println("Did not find carNode in Simulation Controller:: 143");
                    return;
                }
                TranslateTransition move = new TranslateTransition(Duration.millis(300), carNode);

                int intersectionIdx = engine.getIntersectionList().indexOf(intersection);
                if (intersectionIdx < 0) intersectionIdx = 0; // fallback guard

                // horizontal
                if (departure.fromA) {
                    double startY = 200;
                    double startX = 110 + (intersectionIdx) * ROAD_LENGTH;

                    carNode.setLayoutX(startX);
                    carNode.setLayoutY(startY);
                    // Reset any previous translation
                    carNode.setTranslateX(0);
                    carNode.setTranslateY(0);
                    // Animate one road segment
                    move.setByX(ROAD_LENGTH);

                    if (intersectionIdx == (int) view.getAmountOfIntersections()-1) {
                        move.setOnFinished(e -> view.removeCarNode(carNode));
                    }
                    move.play();
                }

                // vertical
                else {
                    double startY = 110;
                    double startX = 160 + (intersectionIdx) * ROAD_LENGTH;
                    carNode.setLayoutX(startX);
                    carNode.setLayoutY(startY);
                    // Reset any previous translation
                    carNode.setTranslateX(0);
                    carNode.setTranslateY(0);
                    // Animate one road segment
                    move.setByY(ROAD_LENGTH);
                    move.setOnFinished(e -> view.removeCarNode(carNode));
                    move.play();
                }
            }
        }
    public IntersectionEngine getEngine() {
        return engine;
    }
}
