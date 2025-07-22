package com.mycompany.dishcover.UI.Component;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class RecipeGrid extends VBox {

    private final FlowPane flowPane;

    public RecipeGrid() {
        ThemeManager.getInstance().registerComponent(this);

        this.setFillWidth(true); // Ensure VBox expands to fill width

        // Initialize the flow pane for recipe cards
        flowPane = new FlowPane();
        flowPane.setPadding(new Insets(20));
        flowPane.setHgap(15);
        flowPane.setVgap(15);
        flowPane.setPrefWrapLength(1000); // Responsive wrapping
        flowPane.setMaxWidth(Double.MAX_VALUE);
        flowPane.prefWidthProperty().bind(this.widthProperty()); // Bind to VBox width

        this.getChildren().add(flowPane);
    }

    /**
     * Clear existing recipe cards and display new ones
     */
    public void displayRecipes(List<Recipe> recipes) {
        flowPane.getChildren().clear();

        for (Recipe recipe : recipes) {
            RecipeCard recipeCard = new RecipeCard(recipe);
            recipeCard.setOnMouseClicked(event -> showRecipeDetails(recipe));
            flowPane.getChildren().add(recipeCard);
        }
    }

    /**
     * Show details for a specific recipe
     */
    private void showRecipeDetails(Recipe recipe) {
        System.out.println("Show details for recipe: " + recipe.getName());
        MainApplication.getInstance().showRecipeDisplay(recipe);
    }
}
