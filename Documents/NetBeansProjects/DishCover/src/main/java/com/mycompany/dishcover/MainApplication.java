package com.mycompany.dishcover;

import com.mycompany.dishcover.Recipe.MealPlan;
import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.UI.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;

public class MainApplication extends Application {

    private static MainApplication instance;
    private Stage primaryStage;

    public static MainApplication getInstance() {
        return instance;
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage();
        Scene scene = new Scene(loginPage);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLogin() {
        showLoginPage();
    }

    public void showSignupPage() {
        SignupPage signupPage = new SignupPage();
        Scene scene = new Scene(signupPage);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showSplashScreen() {
        SplashScreen splashScreen = new SplashScreen();
        Scene scene = new Scene(splashScreen);
        ThemeManager.getInstance().applySavedTheme(scene);
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

        Scene scene = new Scene(scrollPane, 1280, 800);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showRecipeDisplay(Recipe recipe) {
        showRecipeDisplay(recipe, "mainPage");
    }

    public void showRecipeDisplay(Recipe recipe, String source) {
        RecipeDisplay recipeDisplay = new RecipeDisplay(recipe, source);

        ScrollPane scrollPane = new ScrollPane(recipeDisplay);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1280, 800);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showFavoritesPage() {
        FavoritesPage favoritesPage = new FavoritesPage();
        ScrollPane scrollPane = new ScrollPane(favoritesPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1280, 800);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMealPlanPage() {
        MealPlanPage mealPlanPage = new MealPlanPage();
        ScrollPane scrollPane = new ScrollPane(mealPlanPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1280, 800);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showSavedMealPlansPage() {
        SavedMealPlansPage savedPage = new SavedMealPlansPage();
        ScrollPane scrollPane = new ScrollPane(savedPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1280, 800);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMealPlanDetailPage(List<MealPlan> planList) {
        // Provide default back behavior to go to saved plans
        showMealPlanDetailPage(planList, () -> showSavedMealPlansPage());
    }

    public void showMealPlanDetailPage(List<MealPlan> planList, Runnable backAction) {
        MealPlanDetailPage detailPage = new MealPlanDetailPage(planList, backAction);
        ScrollPane scrollPane = new ScrollPane(detailPage);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(scrollPane, 1280, 800);
        ThemeManager.getInstance().applySavedTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;
        primaryStage.setTitle("DishCovery");

        showLogin(); // You can change to showSplashScreen() if needed
    }

    public static void main(String[] args) {
        launch(args);
    }
}
