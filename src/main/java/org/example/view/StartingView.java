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
    private TextField carsInMaxPerGroup;
    private TextField medianArrivalTime;

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

        medianArrivalTime.setPromptText("Median arrival time (s): " + ((config.getConfigValues().get("betweenIntersectionTime") != null) ? config.getConfigValues().get("betweenIntersectionTime") : ""));
  
        TextField timeBetweenIntersection = new TextField();
        timeBetweenIntersection.setPromptText("Time between intersections for a car");

        TextField carReactionTime = new TextField();
        carReactionTime.setPromptText("Car's reaction time");

        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(e -> {
            HomeView homeView = new HomeView();
            Scene homeScene = homeView.buildScene(this); // pass StartingView
            stage.setTitle("Traffic Intersection Control");
            stage.setScene(homeScene);
            stage.sizeToScene();
        });
        Label label = new Label("Choose settings for at least one intersection before starting");
        rootSelection.getChildren().addAll(carsInMaxPerGroup, medianArrivalTime, label, startButton);

        Scene scene = new Scene(rootSelection, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Intersection Simulator");
        stage.show();
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

    @Override
    public void stop() {
        HashMap<String, Long> configValues = new HashMap<>();
        // add any config values you want to save here
        long carsInGroup = getCarsInGroup();
        long medianArrivalTime = getMedianArrivalTime();
        configValues.put("carGroup", carsInGroup);
        System.out.println("carGroup");
        configValues.put("ArrivalTime", medianArrivalTime);

        ConfigController.setConfigValues(configValues);
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

