
package org.example.view;

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

public class HomeView extends Application {

    @Override
    public void start(Stage window) {
        // Intersection visualization
        Pane intersectionPane = new Pane();
        intersectionPane.setPrefSize(400, 400);

        // Draw roads
        Rectangle verticalRoad = new Rectangle(150, 0, 100, 400);
        verticalRoad.setFill(Color.LIGHTGRAY);
        Rectangle horizontalRoad = new Rectangle(0, 150, 400, 100);
        horizontalRoad.setFill(Color.LIGHTGRAY);

        // Traffic lights
        Circle northLight = new Circle(120, 120, 15, Color.RED);
        Circle southLight = new Circle(280, 280, 15, Color.GREEN);
        Circle eastLight = new Circle(280, 120, 15, Color.RED);
        Circle westLight = new Circle(120, 280, 15, Color.GREEN);

        intersectionPane.getChildren().addAll(verticalRoad, horizontalRoad,
                northLight, southLight, eastLight, westLight);

        // Control panel
        HBox controls = new HBox(10);
        Button pauseBtn = new Button("Pause");
        Button resumeBtn = new Button("Resume");
        TextField timeInput = new TextField();
        timeInput.setPromptText("Add time (ms)");
        Button addTimeBtn = new Button("Add Time");

        controls.getChildren().addAll(pauseBtn, resumeBtn, timeInput, addTimeBtn);

        // Layout
        BorderPane root = new BorderPane();
        root.setCenter(intersectionPane);
        root.setBottom(controls);

        Scene view = new Scene(root, 500, 500);
        window.setTitle("Traffic Intersection Control");
        window.setScene(view);
        window.show();

        // Controller
        HomeController controller = new HomeController(this);
        pauseBtn.setOnAction(e -> controller.pauseSimulation());
        resumeBtn.setOnAction(e -> controller.resumeSimulation());
        addTimeBtn.setOnAction(e -> controller.addTime(timeInput.getText()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

