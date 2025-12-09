
package org.example.view;

import javafx.scene.shape.Line;
import org.example.controller.HomeController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.controller.SimulationController;
import org.example.controller.TrafficLightController;
import org.example.framework.IntersectionEngine;
import org.example.model.Queue;
import org.example.model.TrafficLight;

import java.util.Map;

public class HomeView extends Application {

    // Traffic lights
    private Circle northLight;
    private Circle southLight;
    private Circle eastLight;
    private Circle westLight;
    private Pane intersectionPane = new Pane();


    private TimeBetweenIntersection timeToNext;
    private CarReactionTime carReactionTime;


    @Override
    public void start(Stage window) {
        // Intersection visualization
        intersectionPane.setPrefSize(400, 400);

        // Draw roads
        Rectangle verticalRoad = new Rectangle(275, 0, 150, 700);
        verticalRoad.setFill(Color.LIGHTGRAY);
        Rectangle horizontalRoad = new Rectangle(0, 275, 700, 150);
        horizontalRoad.setFill(Color.LIGHTGRAY);
        Rectangle roadCenter = new Rectangle(275,275,150,150);
        roadCenter.setFill(Color.LIGHTGRAY);


        // Draw road lines
        // Vertical
        Line verticalRoadLine = new Line(
                verticalRoad.getX()+verticalRoad.getWidth() / 2, // x1
                verticalRoad.getY(),
                verticalRoad.getX() + verticalRoad.getWidth() / 2,
                verticalRoad.getY() + verticalRoad.getHeight()
                );
        verticalRoadLine.setStroke(Color.WHITE);
        verticalRoadLine.setStrokeWidth(4);
        verticalRoadLine.getStrokeDashArray().addAll(20.0, 15.0); // dash length, gap length

        // Horizontal
        Line horizontalRoadLine = new Line(
                horizontalRoad.getX()+8,                              // x1 left
                horizontalRoad.getY() + horizontalRoad.getHeight() / 2, // y1 at center of road
                horizontalRoad.getX() + horizontalRoad.getWidth(), // x2 right
                horizontalRoad.getY() + horizontalRoad.getHeight() / 2  // y2 at center of road
        );
        horizontalRoadLine.setStroke(Color.WHITE);
        horizontalRoadLine.setStrokeWidth(4);
        horizontalRoadLine.getStrokeDashArray().addAll(20.0, 15.0);


        // Traffic lights

        northLight = new Circle(230, 230, 15);
        southLight = new Circle(460, 460, 15);
        eastLight  = new Circle(460, 230, 15);
        westLight  = new Circle(230, 460, 15);

        intersectionPane.getChildren().addAll(verticalRoad, horizontalRoad,
                verticalRoadLine, horizontalRoadLine, roadCenter,
                northLight, southLight, eastLight, westLight);

        // Control panel
        HBox controls = new HBox(10);
        Button pauseBtn = new Button("Pause");
        Button resumeBtn = new Button("Resume");
        TextField timeInput = new TextField();
        timeInput.setPromptText("Add time (ms)");
        Button addTimeBtn = new Button("Add Time");
        TextField carInput = new TextField();
        carInput.setPromptText("Add cars (amount)");
        Button addCarsBtn = new Button("Add Car");

        // sliders
        timeToNext = new TimeBetweenIntersection();
        carReactionTime = new CarReactionTime();

        controls.getChildren().addAll(
                pauseBtn, resumeBtn, timeInput, addTimeBtn, carInput, addCarsBtn, timeToNext, carReactionTime
        );


        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(intersectionPane);
        root.setBottom(controls);

        Scene view = new Scene(root, 1500, 700);
        window.setTitle("Traffic Intersection Control");
        window.setScene(view);
        window.show();

        // Create Models
        Queue queue = new Queue();
        TrafficLightController trafficLightController = new TrafficLightController();

        // Initialize traffic lights
        updateLights(trafficLightController);

        // Start simulation
        IntersectionEngine intersectionEngine = new IntersectionEngine();
        SimulationController simulationController =
                new SimulationController(intersectionEngine, this);
        simulationController.startSimulation();

        // Controller
        HomeController controller = new HomeController(this, simulationController);
        pauseBtn.setOnAction(e -> controller.pauseSimulation());
        resumeBtn.setOnAction(e -> controller.resumeSimulation());
        addTimeBtn.setOnAction(e -> controller.addTime(timeInput.getText()));
        addCarsBtn.setOnAction(e -> controller.addCars(carInput.getText()));
    }
    public static void main(String[] args) {
        launch(args);
    }


    private static Color toColor(TrafficLight.State s) {
        return switch (s) {
            case RED    -> Color.RED;
            case YELLOW -> Color.YELLOW;
            case GREEN  -> Color.GREEN;
        };
    }



    public void updateLights(TrafficLightController controller) {

        if (northLight == null || southLight == null || eastLight == null || westLight == null) {
            // UI not ready; ignore or log
            System.err.println("Lights not initialized yet; skipping update.");
            return;
        }

        northLight.setFill(toColor(controller.getNorth().getState()));
        southLight.setFill(toColor(controller.getSouth().getState()));
        eastLight.setFill(toColor(controller.getEast().getState()));
        westLight.setFill(toColor(controller.getWest().getState()));
    }


    public void updateQueues(Map<String, Integer> queues) {
        System.out.println("Cars in queue: " + queues);
        // Later can update UI labels or draw cars on the road
    }

    public void addCarNode(Circle carShape) {
        intersectionPane.getChildren().add(carShape);
    }


    // getter: slider values (double)
    public double getTimeBetweenValue() {
        return timeToNext.getTimeBetween();
    }

    public double getCarReactionTime() {
        return carReactionTime.getReactionTime();
    }

}

