package org.example.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import org.example.framework.IntersectionEngine;

public class SimSpeed extends HBox {


    private static final Slider slider = new Slider(0.001, 10, 1); // min, max, initial
    private final Label valueLabel = new Label();

    public SimSpeed() {
        setSpacing(10);
        setPadding(new Insets(10));

        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(30);
        slider.setMinorTickCount(5);

        valueLabel.setText(formatValue(slider.getValue()));
        slider.valueProperty().addListener((obs, oldV, newV) -> {
                    valueLabel.setText(formatValue(newV.doubleValue()));
                    IntersectionEngine.setSimulationSpeed(slider.getValue());
                }
        );
        getChildren().addAll(new Label("Seconds between simulation loops"), slider, valueLabel);
    }

    private String formatValue(double v) {
        return String.format("%.0f s", v);
    }

    // methods
    public static double getSimSpeed() {
        return slider.getValue();
    }
    public static void setSimulationSpeed(double seconds) {
        slider.setValue(seconds);
    }
    public Slider getSimulationSpeedSlider() {
        return slider;
    }
}
