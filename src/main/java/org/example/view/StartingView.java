
package org.example.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

//TODO: Explanation: In here user selects the type of intersections, How many cars in max in one group, Median arrival time


public class StartingView extends Application {

    ComboBox<String> intersection1 = new ComboBox<>();
    ComboBox<String> intersection2 = new ComboBox<>();
    ComboBox<String> intersection3 = new ComboBox<>();
    ComboBox<String> intersection4 = new ComboBox<>();

    @Override
    public void start(Stage stage) {

        var options = javafx.collections.FXCollections.observableArrayList(
                "Has traffic lights",
                "Doesn't have traffic lights",
                "Don't show intersection"
        );

        intersection1.setItems(options);
        intersection2.setItems(options);
        intersection3.setItems(options);
        intersection4.setItems(options);

        intersection1.setPromptText("First intersection");
        intersection2.setPromptText("Second intersection");
        intersection3.setPromptText("Third intersection");
        intersection4.setPromptText("Fourth intersection");

        HBox rootSelection = new HBox();
        rootSelection.setSpacing(10);
        rootSelection.setPadding(new Insets(10));
        rootSelection.getChildren().addAll(intersection1, intersection2, intersection3, intersection4);

        // If user selects "Don't show intersection", not showing the next intersections
        intersection1.setOnAction(e -> handleSelection(intersection1, intersection2, intersection3, intersection4));
        intersection2.setOnAction(e -> handleSelection(intersection2, intersection3, intersection4));
        intersection3.setOnAction(e -> handleSelection(intersection3, intersection4));

        TextField carsInMaxPerGroup = new TextField();
        carsInMaxPerGroup.setPromptText("Cars in max per group");
        TextField medianArrivalTime = new TextField();
        medianArrivalTime.setPromptText("Median arrival time");

        TextField timeBetweenIntersection = new TextField();
        timeBetweenIntersection.setPromptText("Time between intersections for a car");

        TextField carReactionTime = new TextField();
        carReactionTime.setPromptText("Car's reaction time");

        rootSelection.getChildren().addAll(carsInMaxPerGroup, medianArrivalTime, timeBetweenIntersection, carReactionTime);

        // reaktioaika, kuinka pitkään autolla kestää kun valo vaihtunut vihreeksi tai toinen auto mennyt ohi


            Scene scene = new Scene(rootSelection, 1100, 100);
            stage.setScene(scene);
            stage.setTitle("Intersection Simulator");
            stage.show();
    }

    private void handleSelection(ComboBox<String> current, ComboBox<String>... nextIntersections) {
        String value = current.getValue();
        boolean hideNext = "Don't show intersection".equals(value);

        for (ComboBox<String> next : nextIntersections) {
            next.setDisable(hideNext);
            next.setVisible(!hideNext);
            if (hideNext) {
                next.setValue(null);
            }
        }
    }

}
