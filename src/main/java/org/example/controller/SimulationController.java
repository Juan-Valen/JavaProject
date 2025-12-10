
package org.example.controller;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.*;
import org.example.framework.IntersectionEngine;
import org.example.framework.Event;
import org.example.model.*;


import org.example.view.HomeView;

import java.util.*;

public class SimulationController {
    private IntersectionEngine engine;
    private HomeView view;
    private final Map<Car, Circle> carNodes = new HashMap<>();

    private final Map<Car, Deque<Animation>> queues = new HashMap<>();
    private final Set<Car> animating = new HashSet<>();
    private final Map<Car, Boolean> carDirection = new HashMap<>();
    // true = fromA (left→right), false = fromB (right→left)

    private Thread simulationThread;

    private List<HomeView.LightSet> intersectionLights = new ArrayList<>();

    private static final double ROAD_LENGTH = 280;

    // COORDINATES FOR CAR ANIMATION
    private static final double START_X_W = -170;
    private static final double START_Y_W = 200;

    private final double START_X_E = 1350;
    private static final double START_Y_E= 160;



    public SimulationController(IntersectionEngine engine, HomeView view, List<HomeView.LightSet> intersectionLights) {
        this.engine = engine;
        this.view = view;
        this.intersectionLights = intersectionLights;
    }


    public void startSimulation() {
        simulationThread = new Thread(() -> {
            engine.runWithCallback(this::updateView);
        });
        simulationThread.start();
    }



    private void updateView(Event event) {
        Platform.runLater(() -> {

            view.updateQueues(engine.getIntersectionList().get(0).getQueueStates());


            // Get slider-based timing
            long reaction = (long) view.getCarReactionTime();     // human reaction time
            long spacing  = (long) view.getTimeBetweenValue();    // spacing between cars
            long animTime = reaction + spacing;                   // total movement delay

            // ===== LIGHT CHANGE =====
            if (event.getType() == Event.EventType.LIGHT_CHANGE) {
                TrafficLightChange tlc = (TrafficLightChange) event.getPayload();
                tlc.run(event.getTime(), engine.getEventList()); // update model
                for (TrafficLightIntersection inter : engine.getIntersectionList()) {
                    inter.changeLights(); // update its own lights
                }
                refreshIntersectionLights(); // now reads updated intersection lights
            }

            // ===== ARRIVAL =====
            if (event.getType() == Event.EventType.ARRIVAL && event.getPayload() instanceof Arrival) {

                Arrival arrival = (Arrival) event.getPayload();
                Car car = arrival.car;
                carDirection.put(car, arrival.fromA);
                Circle node = new Circle(7, arrival.fromA ? Color.BLUE : Color.RED);

                double totalWidth = ROAD_LENGTH * view.getAmountOfIntersections();

                double startX = arrival.fromA
                        ? START_X_W
                        : totalWidth + 260;

                double startY = arrival.fromA ? START_Y_W : START_Y_E;

                node.setCenterX(startX);
                node.setCenterY(startY);
                view.addCarNode(node);
                carNodes.put(car, node);

                // ARRIVAL movement = 1 intersection
                TranslateTransition arrivalAnim = new TranslateTransition(Duration.millis(animTime*100), node);
                arrivalAnim.setByX(arrival.fromA ? ROAD_LENGTH : -ROAD_LENGTH );
                car.incrementTimesMoved();
                enqueue(car, arrivalAnim);
            }

            // ===== DEPARTURE =====
            if (event.getType() == Event.EventType.DEPARTURE && event.getPayload() instanceof Departure)  {

                Departure departure = (Departure) event.getPayload();
                Car car = departure.car;

                Circle node = carNodes.get(car);
                if (node == null) return;

                TranslateTransition departAnim = new TranslateTransition(Duration.millis(animTime*100), node);
                departAnim.setByX(departure.fromA ? +ROAD_LENGTH : -ROAD_LENGTH);

                enqueue(car, departAnim);

                car.incrementTimesMoved();

                if (car.getTimesMoved() >= view.getAmountOfIntersections()) {
                    carNodes.remove(car);
                    carDirection.remove(car);
                    System.out.println("Car left simulation");
                }
            }
        });
    }


    public IntersectionEngine getEngine() {
        return engine;
    }


    private void enqueue(Car car, Animation anim) {
        queues.computeIfAbsent(car, c -> new ArrayDeque<>()).add(anim);
        if (!animating.contains(car)) {
            playNext(car);
        }
    }

    private void playNext(Car car) {
        Deque<Animation> q = queues.get(car);
        if (q == null || q.isEmpty()) {
            animating.remove(car);
            return;
        }
        animating.add(car);
        Animation next = q.pollFirst();
        next.setOnFinished(e -> {
            // If you need to increment movement count, keep a separate Car registry by ID.
            playNext(car);
        });
        next.play();
    }


    private void refreshIntersectionLights() {
        List<TrafficLightIntersection> intersections = engine.getIntersectionList();

        for (int i = 0; i < intersections.size(); i++) {
            TrafficLightIntersection intersection = intersections.get(i);
            HomeView.LightSet lights = intersectionLights.get(i);

            lights.north.setFill(toColor(intersection.getNorthLight().getState()));
            lights.south.setFill(toColor(intersection.getSouthLight().getState()));
            lights.east.setFill(toColor(intersection.getEastLight().getState()));
            lights.west.setFill(toColor(intersection.getWestLight().getState()));
        }
    }


    private Color toColor(org.example.model.TrafficLight.State state) {
        return switch(state) {
            case RED -> Color.RED;
            case YELLOW -> Color.YELLOW;
            case GREEN -> Color.GREEN;
        };
    }







}
