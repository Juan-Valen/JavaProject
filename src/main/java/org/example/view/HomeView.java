
package org.example.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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


    // Traffic lights
    private Circle northLight;
    private Circle southLight;
    private Circle eastLight;
    private Circle westLight;
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


        controls.getChildren().addAll(
                pauseBtn, resumeBtn, timeInput, addTimeBtn, carInput, addCarsBtn, timeToNext, carReactionTime, simSpeed
        );

        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(intersectionPane);
        root.setBottom(controls);

        Scene view = new Scene(root, 1500, 700);


        // Draw intersections
        for (int i = 0; i < startingView.getAmountOfIntersections(); i++) {
            Pane inter = buildIntersection(i);
            root.getChildren().add(inter);
        }

        // Create Models & Controllers
        TrafficLightController trafficLightController = new TrafficLightController();

        initLights(trafficLightController);

        IntersectionEngine intersectionEngine = new IntersectionEngine(trafficLightController);
        SimulationController simulationController = new SimulationController(intersectionEngine, this);

        intersectionEngine.setIntersections(intersectionTypes);

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
    public Pane buildIntersection(int position){
    Pane interPane = new Pane();
        // Intersection visualization
        interPane.setPrefSize(400, 400);

        // Draw roads
        Rectangle verticalRoad = new Rectangle(140+(140*position*2), 0, 80, 320);
        verticalRoad.setFill(Color.LIGHTGRAY);
        Rectangle horizontalRoad = new Rectangle((140*(position*2)), 140, 320, 80);
        horizontalRoad.setFill(Color.LIGHTGRAY);
        Rectangle roadCenter = new Rectangle(140+(140*position*2), 140, 80, 80);
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

        // Traffic lights
        northLight = new Circle(230, 230, 15);
        southLight = new Circle(460, 460, 15);
        eastLight  = new Circle(460, 230, 15);
        westLight  = new Circle(230, 460, 15);

        interPane.getChildren().addAll(
                verticalRoad, horizontalRoad,
                verticalRoadLine, horizontalRoadLine, roadCenter,
                northLight, southLight, eastLight, westLight
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
        return carReactionTime.getReactionTime();
    }

    public boolean getOpenedFromStartingView(){
        return openedFromStartingView;
    }

}
