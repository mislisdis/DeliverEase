package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.Util.Session;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.util.Objects;

public class SplashScreen extends VBox {
    private Button toggleButton;

    public SplashScreen() {
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

        // 🌟 Logo
        try {
            Image logoImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/splash/img.png")));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(250);
            logoView.setPreserveRatio(true);
            this.getChildren().add(logoView);
        } catch (Exception e) {
            System.err.println("Could not load logo image: " + e.getMessage());
        }

        // 🌟 Personalized Greeting
        String username = Session.getCurrentUsername();
        Label greetingLabel = new Label("Welcome back, " + capitalize(username) + "!");
        greetingLabel.getStyleClass().add("splash-greeting");
        this.getChildren().add(greetingLabel);

        // Spacer
        VBox spacer = new VBox();
        spacer.setPrefHeight(100);
        this.getChildren().add(spacer);

        // Get Started Button
        Button startButton = new Button("Get Started");
        startButton.getStyleClass().add("major-button");
        startButton.setOnAction(event -> MainApplication.getInstance().showMainPage());
        this.getChildren().add(startButton);

        // 🌗 Theme toggle
        toggleButton = new Button();
        toggleButton.getStyleClass().add("toggle-button");
        updateToggleButtonText(ThemeManager.getInstance().isBrightMode());

        ThemeManager.getInstance().brightModeProperty().addListener(
                (observable, oldValue, newValue) -> updateToggleButtonText(newValue));

        toggleButton.setOnAction(event -> ThemeManager.getInstance().toggleTheme());

        this.getChildren().add(toggleButton);
    }

    private void updateToggleButtonText(boolean isBrightMode) {
        toggleButton.setText(isBrightMode ? "Dark Mode" : "Light Mode");
    }

    // Optional helper to capitalize the username
    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return "";
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
