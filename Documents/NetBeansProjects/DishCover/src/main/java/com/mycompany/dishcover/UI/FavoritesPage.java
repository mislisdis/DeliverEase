package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.UI.Component.RecipeCard;
import com.mycompany.dishcover.Util.ApiService;
import com.mycompany.dishcover.Util.Session;
import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class FavoritesPage extends VBox {

    private final FlowPane recipePane;
    private final int userId;

    public FavoritesPage() {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("favorites-container");
        this.setPadding(new Insets(0, 0, 20, 0));
        this.setSpacing(10);

        // Back button styled like minor button / add ingredient button
        Button backButton = new Button("← Back to Main");
        backButton.getStyleClass().add("minor-button"); // changed from back-button
        backButton.setOnAction(e -> MainApplication.getInstance().showMainPage());

        Label title = new Label("Your Favorite Recipes 💖");
        title.getStyleClass().add("section-title");

        VBox header = new VBox(5, backButton, title);
        header.setAlignment(Pos.TOP_LEFT);
        header.setPadding(new Insets(10, 20, 0, 20));

        recipePane = new FlowPane();
        recipePane.setHgap(15);
        recipePane.setVgap(15);
        recipePane.setPadding(new Insets(10, 20, 20, 20));

        ScrollPane scrollPane = new ScrollPane(recipePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        scrollPane.setStyle("-fx-background-color: transparent;");

        this.getChildren().addAll(header, scrollPane);

        userId = Session.getUserId();
        loadFavorites();
    }

    private void loadFavorites() {
        ApiService.getFavoritesAsync(userId).thenAccept(recipes -> {
            Platform.runLater(() -> displayRecipes(recipes));
        });
    }

    private void displayRecipes(List<Recipe> recipes) {
        recipePane.getChildren().clear();

        if (recipes == null || recipes.isEmpty()) {
            Label emptyLabel = new Label("You haven’t saved any recipes yet 😢");
            emptyLabel.getStyleClass().add("favorite-description");
            recipePane.getChildren().add(emptyLabel);
        } else {
            for (Recipe recipe : recipes) {
                RecipeCard card = new RecipeCard(recipe);
                recipePane.getChildren().add(card);
            }
        }
    }
}
