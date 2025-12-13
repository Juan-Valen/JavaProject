
package org.example.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.example.controller.ConfigController;
import org.example.controller.HomeController;
import org.example.controller.SimulationController;
import org.example.controller.TrafficLightController;
import org.example.framework.IntersectionEngine;
import org.example.model.Intersection;
import org.example.model.TrafficLight;
import org.example.model.TrafficLightIntersection;

import java.util.*;

public class HomeView {


    // Traffic lights
    public static class LightSet {
        public Circle north;
        public Circle south;
        public Circle east;
        public Circle west;
    }
    public List<LightSet> intersectionLights = new ArrayList<>();
    TrafficLightController trafficLightController = new TrafficLightController();
    IntersectionEngine intersectionEngine = new IntersectionEngine(trafficLightController, this);

    private Pane intersectionPane = new Pane();
    private StartingView startingView;
    private TimeBetweenIntersection timeToNext;
    private CarReactionTime carReactionTime;
    private SimSpeed simSpeed;

    private boolean openedFromStartingView = false;

    public Scene buildScene(StartingView startingView, List<String> intersectionTypes) {
        if(startingView == null){
            return null;
        }

        openedFromStartingView = true;

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
        simSpeed = new SimSpeed();

        controls.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(controls, Priority.ALWAYS);

        controls.getChildren().addAll(
                pauseBtn, resumeBtn, timeInput, addTimeBtn, carInput, addCarsBtn, timeToNext, carReactionTime, simSpeed
        );

        ScrollPane controlScroll = new ScrollPane(controls);
        controlScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        controlScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        controlScroll.setFitToHeight(true);      // fill the bottom area vertically
        controlScroll.setPannable(true);         // allow click-drag scrolling
        controlScroll.setPrefHeight(100);        // adjust as needed for control height


        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(intersectionPane);
        root.setBottom(controlScroll);
        Scene view = new Scene(root, 1500, 700);

        intersectionEngine.setIntersections(intersectionTypes);

        for (int i = 0; i < intersectionEngine.getIntersectionList().size(); i++ ){
            Pane interPane = buildIntersection(i, intersectionEngine.getIntersectionTypeAt(i), intersectionEngine);
            intersectionPane.getChildren().add(interPane);

        }
        SimulationController simulationController = new SimulationController(intersectionEngine, this);


        simulationController.startSimulation();

        HomeController controller = new HomeController(this, simulationController);
        pauseBtn.setOnAction(e -> controller.pauseSimulation());
        resumeBtn.setOnAction(e -> controller.resumeSimulation());
        addTimeBtn.setOnAction(e -> controller.addTime(timeInput.getText()));
        addCarsBtn.setOnAction(e -> controller.addCars(carInput.getText()));
        return view;
    }

    private static Color toColor(TrafficLight.State s) {
        return switch (s) {
            case RED    -> Color.RED;
            case YELLOW -> Color.YELLOW;
            case GREEN  -> Color.GREEN;
        };
    }

    public void refreshTrafficLights() {
        if(intersectionEngine == null){
            return;
        }
        Platform.runLater(() -> { // <-- ensures UI updates happen on JavaFX thread

            List<Intersection> intersections = intersectionEngine.getIntersectionList();
        int lightIndex = 0; // separate index for intersectionLights

        for (int i = 0; i < intersections.size(); i++) {
            Intersection intersection = intersections.get(i);

            // Only refresh if it is a TrafficLightIntersection
            if (intersection instanceof TrafficLightIntersection tli) {
                LightSet lights = intersectionLights.get(lightIndex);

                    lights.north.setFill(toColor(tli.getNorthLight().getState()));
                    lights.south.setFill(toColor(tli.getSouthLight().getState()));
                    lights.east.setFill(toColor(tli.getEastLight().getState()));
                    lights.west.setFill(toColor(tli.getWestLight().getState()));

                lightIndex++; // only increment when you have a traffic light intersection
            }
        }
        });
    }
    private int getIntersectionIndex(TrafficLightIntersection intersection) {
        for (int i = 0; i < intersectionLights.size(); i++) {
            if (intersectionLights.get(i) != null) {
                return i; // assumes engine and intersectionLights are in sync
            }
        }
        return -1; // not found
    }

    public void updateQueues(Map<String, Integer> queues) {
        System.out.println("Cars in queue: " + queues);
        // Later can update UI labels or draw cars on the road
    }

    public void addCarNode(Circle carShape) {
        intersectionPane.getChildren().add(carShape);
    }
    public void removeCarNode(Circle carNode) {
        if (carNode == null) return;
        Platform.runLater(() -> {
            intersectionPane.getChildren().remove(carNode);
        });
    }

    public Pane buildIntersection(int index, String typeofIntersection ,IntersectionEngine intersectionEngine){
    Pane interPane = new Pane();
        // Intersection visualization
        interPane.setPrefSize(400, 400);

        // Draw roads
        Rectangle verticalRoad = new Rectangle(140+(140*index*2), 0, 80, 320);
        verticalRoad.setFill(Color.LIGHTGRAY);
        Rectangle horizontalRoad = new Rectangle((140*(index*2)), 140, 320, 80);
        horizontalRoad.setFill(Color.LIGHTGRAY);
        Rectangle roadCenter = new Rectangle(140+(140*index*2), 140, 80, 80);
        roadCenter.setFill(Color.LIGHTGRAY);

        // Draw road lines
        Line verticalRoadLine = new Line(
                verticalRoad.getX() + verticalRoad.getWidth() / 2,
                verticalRoad.getY(),
                verticalRoad.getX() + verticalRoad.getWidth() / 2,
                verticalRoad.getY() + verticalRoad.getHeight()
        );
        verticalRoadLine.setStroke(Color.WHITE);
        verticalRoadLine.setStrokeWidth(2);
        verticalRoadLine.getStrokeDashArray().addAll(20.0, 15.0);

        Line horizontalRoadLine = new Line(
                horizontalRoad.getX() + 8,
                horizontalRoad.getY() + horizontalRoad.getHeight() / 2,
                horizontalRoad.getX() + horizontalRoad.getWidth(),
                horizontalRoad.getY() + horizontalRoad.getHeight() / 2
        );
        horizontalRoadLine.setStroke(Color.WHITE);
        horizontalRoadLine.setStrokeWidth(2);
        horizontalRoadLine.getStrokeDashArray().addAll(20.0, 15.0);

        if(typeofIntersection.equals("Traffic Light Intersection")) {
            LightSet lights = new LightSet();
            lights.north = new Circle(120 + index * 280, 120, 8);
            lights.south = new Circle(250 + index * 280, 250, 8);
            lights.east = new Circle(250 + index * 280, 120, 8);
            lights.west = new Circle(120 + index * 280, 250, 8);
            intersectionLights.add(lights);

            interPane.getChildren().addAll(
                    lights.north, lights.south,
                    lights.east, lights.west
            );
        }
        interPane.getChildren().addAll(
                verticalRoad, horizontalRoad,
                verticalRoadLine, horizontalRoadLine, roadCenter
        );

        return interPane;
    }

    public double getAmountOfIntersections(){
            return intersectionEngine.getIntersectionList().size();

    }
      
          // getter: slider values (double)
    public double getTimeBetweenValue() {
        return timeToNext.getTimeBetween();
    }

    public double getCarReactionTime() {
        return CarReactionTime.getReactionTime();
    }

    public boolean getOpenedFromStartingView(){
        return openedFromStartingView;
    }

}
