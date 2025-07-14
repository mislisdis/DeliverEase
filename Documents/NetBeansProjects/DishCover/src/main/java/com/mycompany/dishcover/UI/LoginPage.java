
package com.mycompany.dishcover.UI;


import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LoginPage extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label statusLabel;
    private Button toggleButton;

    public LoginPage() {
        // Register with theme manager
        ThemeManager.getInstance().registerComponent(this);

        this.setWidth(300);

        // Layout setup
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setSpacing(20);
        getStyleClass().add("login-container");

        // Title
        Text title = new Text("Login");
        title.getStyleClass().add("login-title");

        // Create the form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("login-form");

        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("login-label");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("login-input");
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("login-label");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("login-input");
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        // Login button
        loginButton = new Button("Login");
        loginButton.getStyleClass().add("rainbow-button");
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().add(loginButton);
        grid.add(buttonBox, 1, 2);

        // Status label for displaying messages
        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        // Theme toggle button
        toggleButton = new Button();
        toggleButton.getStyleClass().add("toggle-button");
        updateToggleButtonText(ThemeManager.getInstance().isBrightMode());

        // Create a binding to update button text when the theme changes
        ThemeManager.getInstance().brightModeProperty().addListener(
                (observable, oldValue, newValue) -> updateToggleButtonText(newValue));

        toggleButton.setOnAction(event -> ThemeManager.getInstance().toggleTheme());

        // Add components to the layout
        getChildren().addAll(title, grid, statusLabel, toggleButton);

        // Set up action handlers
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

        if (username.isEmpty()) {
            statusLabel.setText("Please enter your username");
            statusLabel.getStyleClass().remove("status-success");
            statusLabel.getStyleClass().add("status-error");
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("Please enter your password");
            statusLabel.getStyleClass().remove("status-success");
            statusLabel.getStyleClass().add("status-error");
            return;
        }

        // Simple credential check for prototype
        if (username.equals("johnexample") && password.equals("example")) {
            statusLabel.setText("Login Successful");
            statusLabel.getStyleClass().remove("status-error");
            statusLabel.getStyleClass().add("status-success");

            // Navigate to splash screen
            try {
                MainApplication.getInstance().showSplashScreen();
            } catch (Exception e) {
                statusLabel.setText("Error navigating to splash screen");
                statusLabel.getStyleClass().remove("status-success");
                statusLabel.getStyleClass().add("status-error");
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Login Failed, please try again");
            statusLabel.getStyleClass().remove("status-success");
            statusLabel.getStyleClass().add("status-error");
        }
    }
}