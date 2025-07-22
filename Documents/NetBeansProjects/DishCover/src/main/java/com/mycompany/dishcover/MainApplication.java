package com.mycompany.dishcover;

import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.UI.LoginPage;
import com.mycompany.dishcover.UI.MainPage;
import com.mycompany.dishcover.UI.RecipeDisplay;
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

    public void showLogin() {
        LoginPage lp = new LoginPage();
        Scene scene = new Scene(lp);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showSplashScreen() {
        SplashScreen ss = new SplashScreen();
        Scene scene = new Scene(ss);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainPage() {
        MainPage mp = new MainPage();
        mp.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());

        ScrollPane sp = new ScrollPane(mp);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(sp, 1280, 800); // Optional: fixed scene size for testing
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showRecipeDisplay(Recipe r) {
        RecipeDisplay rd = new RecipeDisplay(r);

        ScrollPane scrollPane = new ScrollPane(rd);
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
        showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
