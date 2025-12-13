
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


    private static final double ROAD_LENGTH = 280;

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
            car.incrementTimesMoved();
            playNext(car);
        });
        next.play();
    }



    }








