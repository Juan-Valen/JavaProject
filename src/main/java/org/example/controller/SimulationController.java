
package org.example.controller;

import javafx.animation.Animation;
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

    private final Map<Car, Deque<Animation>> queues = new HashMap<>();
    private final Set<Car> animating = new HashSet<>();

    private Thread simulationThread;

    private static final double ROAD_LENGTH = 280;
    private static final int STEPS_TO_NEXT_INTERSECTION = 1;

    // COORDINATES FOR CAR ANIMATION
    private static final double START_X_W = -170;
    private static final double START_Y_W = 200;

    private final double START_X_E = 1350;
    private static final double START_Y_E= 160;



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
            view.updateLights(engine.getTrafficLightController());
            view.updateQueues(engine.getIntersection1().getQueueStates());

            if (event.getType() == Event.EventType.ARRIVAL && event.getPayload() instanceof Arrival) {
                Arrival arrival = (Arrival) event.getPayload();


                Car car = arrival.car;

                Circle carShape = new Circle(7, arrival.fromA ? Color.BLUE : Color.RED);

                double startX = arrival.fromA ? START_X_W : START_X_E + (ROAD_LENGTH - (ROAD_LENGTH * (view.getAmountOfIntersections()+1)) );
                double startY = arrival.fromA ? START_Y_W : START_Y_E;
                double stopX  = arrival.fromA ? startX + ROAD_LENGTH : startX * view.getAmountOfIntersections() ;
                double stopY = startY;


                carShape.setCenterX(startX);
                carShape.setCenterY(startY);
                view.addCarNode(carShape);
                carNodes.put(car, carShape);

                TranslateTransition arrivalAnim = new TranslateTransition(Duration.millis(100), carShape);
                arrivalAnim.setByX(arrival.fromA ? +ROAD_LENGTH : -ROAD_LENGTH);

                enqueue(car, arrivalAnim);

            }


            if (event.getType() == Event.EventType.DEPARTURE && event.getPayload() instanceof Departure) {

                Departure departure = (Departure) event.getPayload();

                Car car = departure.car;

                Circle carNode = carNodes.get(car);
                if (carNode == null){
                    return;
                }



                TranslateTransition departAnim = new TranslateTransition(Duration.millis(100), carNode);
                departAnim.setByX(departure.fromA ? +ROAD_LENGTH : -ROAD_LENGTH);

                enqueue(car, departAnim);
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



}
