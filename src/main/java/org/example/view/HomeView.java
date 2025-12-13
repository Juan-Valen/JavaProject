
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
import org.example.model.TrafficLight;
import org.example.model.TrafficLightIntersection;

import java.util.*;

public class HomeView {
    public static class LightSet {
        public Circle north;
        public Circle south;
        public Circle east;
        public Circle west;
    }

    public List<LightSet> intersectionLights = new ArrayList<>();
    // Traffic lights

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

        // --- Use ONE TrafficLightController for all intersections ---
        TrafficLightController trafficLightController = new TrafficLightController();

        initLights(trafficLightController);

        IntersectionEngine intersectionEngine = new IntersectionEngine(trafficLightController);
        SimulationController simulationController = new SimulationController(intersectionEngine, this);

        intersectionEngine.setIntersections(intersectionTypes);

        List<String> modes = startingView.getIntersectionModes();
        if (modes == null || modes.isEmpty()) return view;

        List<TrafficLightIntersection> intersections = new ArrayList<>();
        for (int i = 0; i < modes.size(); i++) {
            String mode = modes.get(i);
            if (mode == null || mode.equals("Don't show intersection")) continue;

            TrafficLightIntersection intersection = new TrafficLightIntersection(
                    "Intersection-" + i,
                    null,
                    trafficLightController // SAME controller for all
            );
            // Register intersections with engine
            intersectionEngine.addToIntersectionList(intersection);



            // Build UI for intersection
            Pane interPane = buildIntersection(i, mode, intersectionEngine);
            intersectionPane.getChildren().add(interPane);

            // Register the lights with the controller
            trafficLightController.setLightCircles(intersectionLights.get(intersectionLights.size() - 1));
        }


        // Initialize lights
        trafficLightController.setNSGreen();            // NS green at start
        trafficLightController.setGreenDelay(2000);     // 2 sec green
        trafficLightController.setYellowDelay(500);     // 0.5 sec yellow

        SimulationController simulationController = new SimulationController(intersectionEngine, this, intersectionLights);

        // Start simulation
        simulationController.startSimulation();

        // Buttons
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



    public void initLights(TrafficLightController controller) {

        if (northLight == null || southLight == null || eastLight == null || westLight == null) {
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
    public Pane buildIntersection(int index, String mode, IntersectionEngine intersectionEngine) {
        Pane interPane = new Pane();
        interPane.setPrefSize(400, 400);

        // Roads
        Rectangle verticalRoad = new Rectangle(140 + (140 * index * 2), 0, 80, 320);
        verticalRoad.setFill(Color.LIGHTGRAY);

        Rectangle horizontalRoad = new Rectangle((140 * (index * 2)), 140, 320, 80);
        horizontalRoad.setFill(Color.LIGHTGRAY);

        Rectangle roadCenter = new Rectangle(140 + (140 * index * 2), 140, 80, 80);
        roadCenter.setFill(Color.LIGHTGRAY);

        // Road lines
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

        // Create traffic light circles
        LightSet lights = new LightSet();
        lights.north = new Circle(120 + index * 280, 120, 8);
        lights.south = new Circle(250 + index * 280, 250, 8);
        lights.east  = new Circle(250 + index * 280, 120, 8);
        lights.west  = new Circle(120 + index * 280, 250, 8);

        boolean hasLights = mode.equals("Has traffic lights");
        lights.north.setVisible(hasLights);
        lights.south.setVisible(hasLights);
        lights.east.setVisible(hasLights);
        lights.west.setVisible(hasLights);

        intersectionLights.add(lights);

        if (index < intersectionEngine.getIntersectionList().size()) {
            TrafficLightIntersection intersection = intersectionEngine.getIntersectionList().get(index);

            // Set intersection to have its own lights if not yet initialized
            if (intersection.getNorthLight() == null) {
                intersection.setNorthLight(new TrafficLight("NORTH"));
                intersection.setSouthLight(new TrafficLight("SOUTH"));
                intersection.setEastLight(new TrafficLight("EAST"));
                intersection.setWestLight(new TrafficLight("WEST"));
            }

            // Attach UI circles to the intersection for refresh
            intersection.setLightSet(lights);
        } else {
            System.err.println("Invalid intersection index: " + index);
            System.out.println("Size of the list: " + intersectionEngine.getIntersectionList().size());
        }

        interPane.getChildren().addAll(
                verticalRoad, horizontalRoad,
                verticalRoadLine, horizontalRoadLine, roadCenter,
                lights.north, lights.south, lights.east, lights.west
        );

        return interPane;
    }


    public double getAmountOfIntersections(){
        if(startingView == null){
            return 0;
        }
        return startingView.getAmountOfIntersections();
    }
      
          // getter: slider values (double)
    public double getTimeBetweenValue() {
        return timeToNext.getTimeBetween();
    }

    public double getCarReactionTime() {
        return CarReactionTime.getReactionTime();
    }
    public List<LightSet> getIntersectionLights() {
        return intersectionLights;
    }
}
