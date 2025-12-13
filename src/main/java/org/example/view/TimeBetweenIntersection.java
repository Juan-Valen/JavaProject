package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import org.example.framework.IntersectionEngine;

public class TimeBetweenIntersection extends HBox {

    private static final Slider slider = new Slider(1, 600, 120); // min, max, initial
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

        //IntersectionEngine.setTimeToCrossIntersection(getTimeBetween);
    }

    private String formatValue(double v) {
        return String.format("%.0f s", v);
    }

    // methods
    public double getTimeBetween() {
        return slider.getValue();
    }
    public static void setTimeSeconds(double seconds) {
        slider.setValue(seconds);
    }
    public static Slider getTimeBetweenSlider() {
        return slider;
    }
}
