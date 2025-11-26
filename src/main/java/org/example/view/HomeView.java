package org.example.view;

import org.example.controller.HomeController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class HomeView extends Application {

    public void start(Stage window) {

        // Components
        HBox layout = new HBox();
        Button button = new Button("Click me!");

        // Actions
        HomeController controller = new HomeController(this);
        button.setOnAction(e -> controller.testFunc());

        // Display
        Scene view = new Scene(layout, 400, 250);
        view.getStylesheets().add("style.css");
        window.setTitle("Exchange rate");
        window.setScene(view);
        window.show();

    }
}
