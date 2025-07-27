package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.Util.HashUtil;
import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.Util.DatabaseUtil;
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

public class SignupPage extends VBox {
    private TextField emailField, usernameField;
    private PasswordField passwordField;
    private Label statusLabel;

    public SignupPage() {
        ThemeManager.getInstance().registerComponent(this);

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setSpacing(20);
        getStyleClass().add("login-container");

        Text title = new Text("Sign Up");
        title.getStyleClass().add("login-title");

        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.getStyleClass().add("login-form");

        Label emailLabel = new Label("Gmail:");
        emailLabel.getStyleClass().add("login-label");
        emailField = new TextField();
        emailField.setPromptText("Enter your Gmail");
        emailField.getStyleClass().add("login-input");
        form.add(emailLabel, 0, 0);
        form.add(emailField, 1, 0);

        Label usernameLabel = new Label("Username:");
        usernameLabel.getStyleClass().add("login-label");
        usernameField = new TextField();
        usernameField.setPromptText("Create a username");
        usernameField.getStyleClass().add("login-input");
        form.add(usernameLabel, 0, 1);
        form.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        passwordLabel.getStyleClass().add("login-label");
        passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.getStyleClass().add("login-input");
        form.add(passwordLabel, 0, 2);
        form.add(passwordField, 1, 2);

        Button signupButton = new Button("Create Account");
        signupButton.getStyleClass().add("rainbow-button");
        signupButton.setOnAction(e -> handleSignup());

        HBox buttonBox = new HBox(signupButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        form.add(buttonBox, 1, 3);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("status-label");

        Button toggleButton = new Button();
        toggleButton.getStyleClass().add("toggle-button");
        updateToggleText(toggleButton, ThemeManager.getInstance().isBrightMode());
        toggleButton.setOnAction(event -> {
            ThemeManager.getInstance().toggleTheme();
            updateToggleText(toggleButton, ThemeManager.getInstance().isBrightMode());
        });

        // Login Link
        Text loginLink = new Text("Already have an account? Log in");
        loginLink.getStyleClass().add("footer-link");
        loginLink.setOnMouseClicked(e -> MainApplication.getInstance().showLoginPage());

        getChildren().addAll(title, form, statusLabel, loginLink, toggleButton);
    }

    private void updateToggleText(Button button, boolean isBright) {
        button.setText(isBright ? "Switch to Dark Mode" : "Switch to Light Mode");
    }

    private void handleSignup() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!email.endsWith("@gmail.com")) {
            showError("Only Gmail accounts allowed.");
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password required.");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String checkQuery = "SELECT * FROM users WHERE email = ? OR username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                showError("Email or Username already exists.");
                return;
            }

            String insertQuery = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, email);
            insertStmt.setString(2, username);
            insertStmt.setString(3, HashUtil.hashPassword(password));
            insertStmt.executeUpdate();

            showSuccess("Signup successful! You can now login.");
            MainApplication.getInstance().showLoginPage();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
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
