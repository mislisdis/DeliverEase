package com.mycompany.dishcover;

import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.UI.LoginPage;
import com.mycompany.dishcover.UI.MainPage;
import com.mycompany.dishcover.UI.RecipeDisplay;
import com.mycompany.dishcover.UI.SignupPage;
import com.mycompany.dishcover.UI.SplashScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private static MainApplication instance;
    private Stage primaryStage;

    public static MainApplication getInstance() {
        return instance;
    }

    // ✅ Show Login Page
    public void showLoginPage() {
        LoginPage loginPage = new LoginPage();
        Scene scene = new Scene(loginPage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLogin() {
        showLoginPage(); // optional: delegate to new method
    }

    // ✅ Show Signup Page
    public void showSignupPage() {
        SignupPage signupPage = new SignupPage();
        Scene scene = new Scene(signupPage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showSplashScreen() {
        SplashScreen splashScreen = new SplashScreen();
        Scene scene = new Scene(splashScreen);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainPage() {
        MainPage mainPage = new MainPage();
        mainPage.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());

        ScrollPane scrollPane = new ScrollPane(mainPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1280, 800); // Optional: fixed scene size
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showRecipeDisplay(Recipe recipe) {
        RecipeDisplay recipeDisplay = new RecipeDisplay(recipe);

        ScrollPane scrollPane = new ScrollPane(recipeDisplay);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        primaryStage.setTitle("DishCovery");

        showLogin(); // or showSplashScreen(); if you want intro first
    }

    public static void main(String[] args) {
        launch(args);
    }
}
