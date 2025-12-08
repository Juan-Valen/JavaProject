package org.example.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class IntersectionView extends Pane {

    private Circle northLight, southLight, eastLight, westLight;

    public IntersectionView() {
        setPrefSize(320, 320);
        drawRoads();
        drawLights();
    }

    private void drawRoads() {
        Rectangle vertical = new Rectangle(140, 0, 40, 320);
        vertical.setFill(Color.LIGHTGRAY);

        Rectangle horizontal = new Rectangle(0, 140, 320, 40);
        horizontal.setFill(Color.LIGHTGRAY);

        Rectangle center = new Rectangle(140, 140, 40, 40);
        center.setFill(Color.DARKGRAY);

        getChildren().addAll(vertical, horizontal, center);
    }


    private void drawLights() {
        northLight = new Circle(160, 120, 10, Color.RED);
        southLight = new Circle(160, 200, 10, Color.RED);
        eastLight  = new Circle(200, 160, 10, Color.RED);
        westLight  = new Circle(120, 160, 10, Color.RED);

        getChildren().addAll(northLight, southLight, eastLight, westLight);
    }

    public void setLayoutPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }
}
