
package com.mycompany.dishcover.UI;


import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.util.Objects;

public class SplashScreen extends VBox {
    private Button toggleButton;

    public SplashScreen() {
        // Register this component with the ThemeManager
        ThemeManager.getInstance().registerComponent(this);

        this.setPrefWidth(400);
        this.setPrefHeight(500);
        this.setMaxHeight(600);

        showSplashScreen();
    }

    private void showSplashScreen() {
        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30, 30, 30, 30));
        this.getStyleClass().add("gradient-background");

        try {
            Image logoImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/com/hci/javafx/image/logos/DishCovery.png")));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(250);
            logoView.setPreserveRatio(true);
            this.getChildren().add(logoView);
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        VBox spacer = new VBox();
        spacer.setPrefHeight(100);
        this.getChildren().add(spacer);

        Button startButton = new Button("Get Started");
        startButton.getStyleClass().add("major-button");
        startButton.setOnAction(event -> MainApplication.getInstance().showMainPage());
        this.getChildren().add(startButton);

        // Add theme toggle button
        toggleButton = new Button();
        toggleButton.getStyleClass().add("toggle-button");
        updateToggleButtonText(ThemeManager.getInstance().isBrightMode());

        // Create a binding to update button text when the theme changes
        ThemeManager.getInstance().brightModeProperty().addListener(
                (observable, oldValue, newValue) -> updateToggleButtonText(newValue));

        toggleButton.setOnAction(event -> ThemeManager.getInstance().toggleTheme());

        this.getChildren().add(toggleButton);
    }

    private void updateToggleButtonText(boolean isBrightMode) {
        toggleButton.setText(isBrightMode ? "Dark Mode" : "Light Mode");
    }
}