package org.example.controller;

import org.example.framework.IntersectionEngine;
import org.example.view.HomeView;

// Hooks UI to simulation
public class HomeController {
    private HomeView view;
    private SimulationController simulationController;
    public HomeController(HomeView view, SimulationController simulationController) {
        this.view = view;
        this.simulationController = simulationController;
    }

    public void testFunc() {
        System.out.println("Test");
    }

    public void pauseSimulation() {
        simulationController.getEngine().setPaused(true);
    }
    public void resumeSimulation() {
        simulationController.getEngine().setPaused(false);
    }

    public void addTime(String time) {
        double addedTime = Double.parseDouble(time);
        IntersectionEngine.addSimulationTime(addedTime);
    }


    public void addCars(String num) {
        int amount = Integer.parseInt(num);
        System.out.println("Add cars:" + amount);
    }
}