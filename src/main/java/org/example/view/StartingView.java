package org.example.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.ConfigController;
import org.example.framework.IntersectionEngine;
import org.example.model.Config;
import org.example.view.HomeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartingView extends Application {
    ComboBox<String> intersection1 = new ComboBox<>();
    ComboBox<String> intersection2 = new ComboBox<>();
    ComboBox<String> intersection3 = new ComboBox<>();
    ComboBox<String> intersection4 = new ComboBox<>();
    private int amountOfIntersections = 4; // default

    TextField carsInMaxPerGroup = new TextField();
    TextField medianArrivalTime = new TextField();
    TextField simulationDuration = new TextField();

    HomeView homeView = new HomeView();

    @Override
    public void start(Stage stage) {

        Config config = ConfigController.getConfig();

        var options = javafx.collections.FXCollections.observableArrayList(
                "Has traffic lights",
                "Doesn't have traffic lights",
                "Don't show intersection"
        );

        // Set options
        intersection1.setItems(options);
        intersection2.setItems(options);
        intersection3.setItems(options);
        intersection4.setItems(options);

        // option values instead so every intersection has option set for it
        intersection1.setPromptText(options.get(0));
        intersection2.setPromptText(options.get(0));
        intersection3.setPromptText(options.get(0));
        intersection4.setPromptText(options.get(0));

        VBox rootSelection = new VBox();
        rootSelection.setSpacing(10);
        rootSelection.setPadding(new Insets(10));
        rootSelection.getChildren().addAll(intersection1, intersection2, intersection3, intersection4);

        // Whenever a selection changes, recalculate amount of intersections
        intersection1.setOnAction(e -> recalculateIntersections());
        intersection2.setOnAction(e -> recalculateIntersections());
        intersection3.setOnAction(e -> recalculateIntersections());
        intersection4.setOnAction(e -> recalculateIntersections());

        carsInMaxPerGroup = new TextField();
        carsInMaxPerGroup.setPromptText("Cars in max per group: " + ((config.getConfigValues().get("carGroupAvgSize") != null) ? config.getConfigValues().get("carGroupAvgSize") : ""));
        medianArrivalTime = new TextField();

        carsInMaxPerGroup.setPromptText("Cars in max per group: ");
        carsInMaxPerGroup.setText(String.valueOf((config.getConfigValues().get("carGroupAvgSize") != null) ? config.getConfigValues().get("carGroupAvgSize") : ""));

        medianArrivalTime.setPromptText("Median arrival time (s): ");
        medianArrivalTime.setText(String.valueOf((config.getConfigValues().get("avgCarArrivalInterval") != null) ? config.getConfigValues().get("avgCarArrivalInterval") : ""));

        simulationDuration.setPromptText("Simulation time (s): ");
        simulationDuration.setText(String.valueOf((config.getConfigValues().get("simulationDuration") != null) ? config.getConfigValues().get("simulationDuration") : ""));
  
        TextField timeBetweenIntersection = new TextField();
        timeBetweenIntersection.setPromptText("Time between intersections for a car");

        TextField carReactionTime = new TextField();
        carReactionTime.setPromptText("Car's reaction time");

        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(e -> {
            // set intersection engine parameters
            IntersectionEngine.setCarArrivalIntervalDist(Integer.parseInt(medianArrivalTime.getText()));

            IntersectionEngine.setCarGroupSizeDist(Integer.parseInt(carsInMaxPerGroup.getText()));

            IntersectionEngine.setSimulationDuration(Integer.parseInt(simulationDuration.getText()));

            Scene homeScene = homeView.buildScene(this, getIntersections()); // pass StartingView

            // set saved values from starting view to home view sliders
            if (config.getConfigValues().get("betweenIntersectionTime") != null) {
                TimeBetweenIntersection.setTimeSeconds(config.getConfigValues().get("betweenIntersectionTime"));
            }
            if (config.getConfigValues().get("carReactionTime") != null) {
                CarReactionTime.setReactionTime(config.getConfigValues().get("carReactionTime"));
            }

            stage.setTitle("Traffic Intersection Control");
            stage.setScene(homeScene);
            stage.sizeToScene();


        });
        Label label = new Label("Choose settings for at least one intersection before starting");
        rootSelection.getChildren().addAll(carsInMaxPerGroup, medianArrivalTime, label, startButton);

        rootSelection.getChildren().addAll(carsInMaxPerGroup, medianArrivalTime, simulationDuration, startButton);

//        Scene scene = new Scene(rootSelection, 1100, 1000);
        Scene scene = new Scene(rootSelection);
        stage.setScene(scene);

        stage.sizeToScene();

        stage.setTitle("Intersection Simulator");
        stage.show();

        // Ensure complete application exit on window close
        Platform.setImplicitExit(true);
        stage.setOnCloseRequest(event -> {;
            Platform.exit();
        });
    }

    /** Recalculate number of intersections based on current selections */
    private void recalculateIntersections() {
        amountOfIntersections = 0;

        if (!"Don't show intersection".equals(intersection1.getValue())) amountOfIntersections++;
        if (!"Don't show intersection".equals(intersection2.getValue())) amountOfIntersections++;
        if (!"Don't show intersection".equals(intersection3.getValue())) amountOfIntersections++;
        if (!"Don't show intersection".equals(intersection4.getValue())) amountOfIntersections++;

        System.out.println("Amount of intersections: " + amountOfIntersections);
    }

    public int getAmountOfIntersections() {
        recalculateIntersections();
        return amountOfIntersections;
    }
    public List<String> getIntersectionModes() {
        List<String> result = new ArrayList<>();
        if (!"Don't show intersection".equals(intersection1.getValue()))
            result.add(intersection1.getValue());
        if (!"Don't show intersection".equals(intersection2.getValue()))
            result.add(intersection2.getValue());
        if (!"Don't show intersection".equals(intersection3.getValue()))
            result.add(intersection3.getValue());
        if (!"Don't show intersection".equals(intersection4.getValue()))
            result.add(intersection4.getValue());
        return result;
    }

    public List<String> getIntersections() {
        List<String> intersectionList = new ArrayList<>();
        switch (intersection1.getValue()) {
            case "Has traffic lights" -> intersectionList.add("Traffic Light Intersection");
            case "Doesn't have traffic lights" -> intersectionList.add("Bare Intersection");
        }

        if (!"Don't show intersection".equals(intersection2.getValue()) && intersection2.getValue() != null) {
            switch (intersection2.getValue()) {
                case "Has traffic lights" -> intersectionList.add("Traffic Light Intersection");
                case "Doesn't have traffic lights" -> intersectionList.add("Bare Intersection");
            }
        }

        if (!"Don't show intersection".equals(intersection3.getValue()) && intersection3.getValue() != null) {
            switch (intersection3.getValue()) {
                case "Has traffic lights" -> intersectionList.add("Traffic Light Intersection");
                case "Doesn't have traffic lights" -> intersectionList.add("Bare Intersection");
            }
        }

        if (!"Don't show intersection".equals(intersection4.getValue()) && intersection4.getValue() != null) {
            switch (intersection4.getValue()) {
                case "Has traffic lights" -> intersectionList.add("Traffic Light Intersection");
                case "Doesn't have traffic lights" -> intersectionList.add("Bare Intersection");
            }
        }
        return intersectionList;
    }

    @Override
    public void stop() {
        System.out.println("Application is stopping, saving configuration...");
        HashMap<String, Long> configValues = new HashMap<>();
        // add any config values you want to save here
        if (!carsInMaxPerGroup.getText().isEmpty() && !medianArrivalTime.getText().isEmpty() && !simulationDuration.getText().isEmpty()) {
            long carsInGroup = Long.parseLong(carsInMaxPerGroup.getText());
            long medianArrivalTimeVal = Long.parseLong(medianArrivalTime.getText());
            long simDuration = Long.parseLong(simulationDuration.getText());
            configValues.put("carGroupAvgSize", carsInGroup);
            configValues.put("avgCarArrivalInterval", medianArrivalTimeVal);
            configValues.put("simulationDuration", simDuration);
        }

        if (homeView.getOpenedFromStartingView()) {
            System.out.println("Saving time between intersections: " + Math.round(homeView.getTimeBetweenValue()));
        configValues.put("betweenIntersectionTime" , Math.round(homeView.getTimeBetweenValue()));
        }
        if (homeView.getOpenedFromStartingView()) {
        configValues.put("carReactionTime" , Math.round(homeView.getCarReactionTime()));
        }

        ConfigController.setConfigValues(configValues);

        System.exit(0);
    }


    // getters for starting view car group and arrival inputs
    public long getCarsInGroup() {
        if (carsInMaxPerGroup == null || carsInMaxPerGroup.getText().isEmpty()) {
            return 0;
        }
        return Long.parseLong(carsInMaxPerGroup.getText());
    }

    public long getMedianArrivalTime() {
        if (medianArrivalTime == null || medianArrivalTime.getText().isEmpty()) {
            return 0;
        }
        return Long.parseLong(medianArrivalTime.getText());
    }

}

