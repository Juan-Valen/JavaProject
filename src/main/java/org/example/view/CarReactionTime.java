package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class CarReactionTime extends HBox {

    private final Slider slider = new Slider(0, 10, 3); // min, max, initial
    private final Label valueLabel = new Label();

    public CarReactionTime() {
        setSpacing(10);
        setPadding(new Insets(10));

        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(30);
        slider.setMinorTickCount(5);

        valueLabel.setText(formatValue(slider.getValue()));
        slider.valueProperty().addListener((obs, oldV, newV) ->
                valueLabel.setText(formatValue(newV.doubleValue()))
        );

        getChildren().addAll(new Label("Car's reaction time"), slider, valueLabel);
    }

    private String formatValue(double v) {
        return String.format("%.0f s", v);
    }

    // methods
    public double getReactionTime() {
        return slider.getValue();
    }
    public void setReactionTime(double seconds) {
        slider.setValue(seconds);
    }
    public Slider getReactionSlider() {
        return slider;
    }
}
