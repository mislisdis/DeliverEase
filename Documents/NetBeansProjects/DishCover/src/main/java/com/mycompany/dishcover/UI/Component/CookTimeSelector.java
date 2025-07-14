
package com.mycompany.dishcover.UI.Component;



import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CookTimeSelector extends VBox {

    private TextField timeInput;

    public CookTimeSelector() {
        ThemeManager.getInstance().registerComponent(this);

        this.setSpacing(10);
        this.setPadding(new Insets(5, 15, 15, 15));

        createCookTimeSection();
    }

    private void createCookTimeSection() {
        // Create label for cook time
        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER_LEFT);
        labelContainer.setSpacing(10);

        // Create the timer icon (using JavaFX shape as placeholder)
        javafx.scene.shape.Circle timerIcon = new javafx.scene.shape.Circle(8);
        timerIcon.getStyleClass().add("cook-time-icon");

        // Create the "Cook Time" label
        Label cookTimeLabel = new Label("Cook Time");
        cookTimeLabel.getStyleClass().add("cook-time-text");

        labelContainer.getChildren().addAll(timerIcon, cookTimeLabel);

        // Create time input field
        HBox inputContainer = new HBox();
        inputContainer.setAlignment(Pos.CENTER_RIGHT);

        timeInput = new TextField();
        timeInput.getStyleClass().add("cook-time-input");
        timeInput.setPrefWidth(100);
        timeInput.setPromptText("e.g 15");

        // Only allow numbers to be entered
        timeInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                timeInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Add "mins" label
        Label minsLabel = new Label("mins");
        minsLabel.setPadding(new Insets(0, 0, 0, 5));

        inputContainer.getChildren().addAll(minsLabel);

        // Create container for the label and input field
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(10);
        container.getChildren().addAll(labelContainer, timeInput, inputContainer);

        this.getChildren().add(container);
    }

    /**
     * Get the selected cook time in minutes
     */
    public int getCookTimeMinutes() {
        try {
            return Integer.parseInt(timeInput.getText());
        } catch (NumberFormatException e) {
            return 15; // Default value
        }
    }
}