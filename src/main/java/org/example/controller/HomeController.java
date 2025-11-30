package org.example.controller;

import org.example.view.HomeView;

// Hooks UI to simulation
public class HomeController {
    private HomeView view;

    public HomeController(HomeView view) {
        this.view = view;

    }

    public void testFunc() {
        System.out.println("Test");
    }

    public void pauseSimulation() {
        System.out.println("Pause");
    }

    public void resumeSimulation() {
        System.out.println("Resume");
    }

    public void addTime(String time) {
        System.out.println("add time to simulation " + time);
    }


    public void addCars(String num) {
        int amount = Integer.parseInt(num);
        System.out.println("Add cars:" + amount);
    }
}