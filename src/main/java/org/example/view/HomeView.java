
package org.example.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.example.controller.HomeController;
import org.example.controller.SimulationController;
import org.example.controller.TrafficLightController;
import org.example.framework.IntersectionEngine;
import org.example.model.TrafficLight;

import java.util.Map;

public class HomeView {


    private Pane intersectionPane = new Pane();
    private StartingView startingView;

    public Scene buildScene(StartingView startingView) {
        if(startingView == null){
            return null;
        }
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
        controls.getChildren().addAll(pauseBtn, resumeBtn, timeInput, addTimeBtn, carInput, addCarsBtn);

        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(intersectionPane);
        root.setBottom(controls);

        Scene scene = new Scene(root, 1000, 600);


        // Check for the amount of intersections
        for (int i = 0; i < startingView.getAmountOfIntersections(); i++) {
            Pane inter = buildIntersection(i);
            intersectionPane.getChildren().addAll(inter.getChildren());
        }


        // Create Models & Controllers
        TrafficLightController trafficLightController = new TrafficLightController();

        // Initialize traffic lights
        updateLights(trafficLightController);

        // Start simulation
        IntersectionEngine intersectionEngine = new IntersectionEngine();
        SimulationController simulationController = new SimulationController(intersectionEngine, this);
        simulationController.startSimulation();

        // Controller
        HomeController controller = new HomeController(this, simulationController);
        pauseBtn.setOnAction(e -> controller.pauseSimulation());
        resumeBtn.setOnAction(e -> controller.resumeSimulation());
        addTimeBtn.setOnAction(e -> controller.addTime(timeInput.getText()));
        addCarsBtn.setOnAction(e -> controller.addCars(carInput.getText()));

        return scene;
    }

    private static Color toColor(TrafficLight.State s) {
        return switch (s) {
            case RED    -> Color.RED;
            case YELLOW -> Color.YELLOW;
            case GREEN  -> Color.GREEN;
        };
    }

    public void updateLights(TrafficLightController controller) {
        System.out.println("Update lights");

        /*if (northLight == null || southLight == null || eastLight == null || westLight == null) {
            System.err.println("Lights not initialized yet; skipping update.");
            return;
        }
        northLight.setFill(toColor(controller.getNorth().getState()));
        southLight.setFill(toColor(controller.getSouth().getState()));
        eastLight.setFill(toColor(controller.getEast().getState()));
        westLight.setFill(toColor(controller.getWest().getState()));*/
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
        Circle northLight = new Circle(230, 230, 15);
        Circle southLight = new Circle(460, 460, 15);
        Circle eastLight  = new Circle(460, 230, 15);
        Circle westLight  = new Circle(230, 460, 15);

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

}
