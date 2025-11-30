package org.example.controller;


import javafx.animation.AnimationTimer;
import org.example.model.Queue;
import org.example.model.TrafficLight;
import org.example.view.HomeView;

public class SimulationController {
    private long lastUpdate = 0;
    private TrafficLightController trafficLightController;
    private Queue queue;
    private HomeView view;

    public SimulationController(TrafficLightController trafficLightController, Queue queue, HomeView view) {
        this.trafficLightController = trafficLightController;
        this.queue = queue;
        this.view = view;
    }

    public void startSimulation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double elapsedSeconds = (now - lastUpdate) / 1_000_000_000.0;
                if (elapsedSeconds >= 1) { // update every second
                    updateSimulation(elapsedSeconds);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void updateSimulation(double elapsedSeconds) {
        // Example logic:
        trafficLightController.update(elapsedSeconds);

        //queue.updateQueues();

        // Log events
        System.out.println("Time: " + System.currentTimeMillis());
        System.out.println("North traffic light state: " + trafficLightController.getNorthState());
        System.out.println("South traffic light state: " + trafficLightController.getSouthState());
        System.out.println("East traffic light state: " + trafficLightController.getEastState());
        System.out.println("West traffic light state: " + trafficLightController.getWestState());

        System.out.println("Cars in queue: " + " ---Queue size here--- ");

        // Update view
        view.updateLights(trafficLightController);
        //view.updateQueues(queueModel);
    }
}

