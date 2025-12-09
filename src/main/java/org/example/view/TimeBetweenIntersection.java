package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;

public class TimeBetweenIntersection extends HBox {

    private final Slider slider = new Slider(0, 10, 2); // min, max, initial
    private final Label valueLabel = new Label();

    public TimeBetweenIntersection() {
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

        getChildren().addAll(new Label("Time between intersections:"), slider, valueLabel);
    }

    private String formatValue(double v) {
        return String.format("%.0f s", v);
    }

    // methods
    public double getTimeBetween() {
        return slider.getValue();
    }
    public void setTimeSeconds(double seconds) {
        slider.setValue(seconds);
    }
    public Slider getTimeBetweenSlider() {
        return slider;
    }
}
