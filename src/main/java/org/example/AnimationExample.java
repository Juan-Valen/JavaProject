package org.example;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * A simple JavaFX animation examples. Animates a Circle's X property by
 * translating (moving) it 200 points over 10 seconds.
 */

public class AnimationExample extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        Circle circle = new Circle(50, 80, 50, Color.RED);
        Circle circle2 = new Circle(50, 80, 50, Color.GRAY);

        // change circle.translateXProperty from it's current value to 200
        KeyValue keyValue = new KeyValue(circle.translateXProperty(), 200);
        KeyValue keyValue2 = new KeyValue(circle2.translateYProperty(), 200);

        // over the course of 5 seconds
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(10), keyValue);
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(10), keyValue2);
        Timeline timeline2 = new Timeline(keyFrame2);
        Timeline timeline = new Timeline(keyFrame);

        Scene scene = new Scene(new Pane(circle), 300, 250);
        Scene scene2 = new Scene(new Pane(circle2), 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setScene(scene2);
        primaryStage.show();

        timeline.play();
        timeline2.play();

    }
}