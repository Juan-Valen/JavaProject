
package org.example.controller;

import org.example.framework.IntersectionEngine;
import org.example.model.Intersection;
import org.example.view.HomeView;

public class SimulationController {
    private IntersectionEngine engine;
    private HomeView view;

    public SimulationController(IntersectionEngine engine, HomeView view) {
        this.engine = engine;
        this.view = view;
    }

    public void startSimulation() {
        // Run the engine in a separate thread so UI stays responsive
        new Thread(() -> {
            engine.runWithCallback(this::updateView);
        }).start();
    }

    private void updateView() {
        // Get intersection state from engine
        Intersection intersection = engine.getIntersection1();

        // Update lights in the view
        view.updateLights(intersection.getTrafficLights());

        // Update queues
        view.updateQueues(intersection.getQueueStates());

        // Optional: Log for debugging
        System.out.println("Updated view at simulation time: " + engine.getCurrentTime() + " ms");
        System.out.println("---------------------------");
    }
}


