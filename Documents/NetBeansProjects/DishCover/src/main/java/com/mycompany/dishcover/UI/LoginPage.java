package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Util.DatabaseUtil;
import com.mycompany.dishcover.Util.HashUtil;
import com.mycompany.dishcover.Util.Session;
import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginPage extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label statusLabel;
    private Button toggleButton;

    public LoginPage() {
        ThemeManager.getInstance().registerComponent(this);
        this.setWidth(300);

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setSpacing(20);
        getStyleClass().add("login-container");

        Text title = new Text("Login");
        title.getStyleClass().add("login-title");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("login-form");

        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("login-label");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("login-input");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("login-label");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("login-input");
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        loginButton = new Button("Login");
        loginButton.getStyleClass().add("rainbow-button");
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(loginButton);
        grid.add(buttonBox, 1, 2);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        // Toggle Theme Button
        toggleButton = new Button();
        toggleButton.getStyleClass().add("toggle-button");
        updateToggleButtonText(ThemeManager.getInstance().isBrightMode());
        ThemeManager.getInstance().brightModeProperty().addListener(
                (observable, oldValue, newValue) -> updateToggleButtonText(newValue));
        toggleButton.setOnAction(event -> ThemeManager.getInstance().toggleTheme());

        // Sign up link
        Text signupLink = new Text("Don't have an account? Sign Up");
        signupLink.getStyleClass().add("footer-link");
        signupLink.setOnMouseClicked(e -> MainApplication.getInstance().showSignupPage());

        getChildren().addAll(title, grid, statusLabel, signupLink, toggleButton);
        setupEventHandlers();
    }

    private void updateToggleButtonText(boolean isBrightMode) {
        toggleButton.setText(isBrightMode ? "Switch to Dark Mode" : "Switch to Light Mode");
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, HashUtil.hashPassword(password));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Session.setCurrentUsername(username);
                showSuccess("Login successful!");
                MainApplication.getInstance().showSplashScreen();
            } else {
                showError("Invalid username or password.");
            }
        } catch (Exception ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().remove("status-success");
        statusLabel.getStyleClass().add("status-error");
    }

    private void showSuccess(String msg) {
        statusLabel.setText(msg);
        statusLabel.getStyleClass().remove("status-error");
        statusLabel.getStyleClass().add("status-success");
    }
}
