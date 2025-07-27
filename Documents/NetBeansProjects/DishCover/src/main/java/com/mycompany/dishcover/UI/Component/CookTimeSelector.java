package com.mycompany.dishcover.UI.Component;

import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class CookTimeSelector extends VBox {

    private TextField timeInput;

    public CookTimeSelector() {
        ThemeManager.getInstance().registerComponent(this);

        this.setSpacing(10);
        this.setPadding(new Insets(10, 20, 15, 20));
        this.setAlignment(Pos.TOP_LEFT);

        // Build components
        Label cookTimeLabel = createCookTimeLabel();
        HBox timeInputSection = createTimeInputSection();

        this.getChildren().addAll(cookTimeLabel, timeInputSection);
    }

    private Label createCookTimeLabel() {
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER_LEFT);
        labelBox.setSpacing(8);

        // Timer icon
        Circle timerIcon = new Circle(6);
        timerIcon.getStyleClass().add("cook-time-icon");

        Label label = new Label("Cook Time");
        label.getStyleClass().add("cook-time-text");

        labelBox.getChildren().addAll(timerIcon, label);
        this.getChildren().add(labelBox);

        return label;
    }

    private HBox createTimeInputSection() {
        timeInput = new TextField();
        timeInput.getStyleClass().add("cook-time-input");
        timeInput.setPromptText("e.g. 15");

        // Allow only digits
        timeInput.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                timeInput.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        Label minsLabel = new Label("mins");
        minsLabel.setPadding(new Insets(0, 0, 0, 6));
        minsLabel.getStyleClass().add("non-visual-text");

        HBox container = new HBox(timeInput, minsLabel);
        container.setSpacing(5);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }

    /**
     * Get the selected cook time in minutes
     */
    public int getCookTimeMinutes() {
        try {
            return Integer.parseInt(timeInput.getText().trim());
        } catch (NumberFormatException e) {
            return 0; // Return 0 if input is empty or invalid
        }
    }

    /**
     * Check if the user has entered a valid cook time
     */
    public boolean isUserInputGiven() {
        return !timeInput.getText().trim().isEmpty();
    }
}
