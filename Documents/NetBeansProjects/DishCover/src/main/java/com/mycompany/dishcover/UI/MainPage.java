package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Recipe.RecipeFilter;
import com.mycompany.dishcover.Recipe.RecipeService;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.UI.Component.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class MainPage extends VBox {

    private SearchBar searchBar;
    private FilterComponent filterComponent;
    private CookTimeSelector cookTimeSelector;
    private IngredientSearchTab ingredientSearchTab;
    private RecipeGrid recipeGrid;
    private RecipeService recipeService;

    public MainPage() {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("gradient-background");
        this.setPadding(new Insets(0, 0, 20, 0));
        this.setSpacing(10);

        // Initialize recipe service
        recipeService = new RecipeService();

        showHeader();
        showSearchComponents();
        showFindRecipesButton();

        // Create scrollable recipe grid
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        recipeGrid = new RecipeGrid();
        scrollPane.setContent(recipeGrid);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        this.getChildren().add(scrollPane);

        Footer ft = new Footer();
        this.getChildren().add(ft);
    }

    private void showHeader() {
        HeaderCard headerCard = new HeaderCard();
        this.getChildren().add(headerCard);
    }

    private void showSearchComponents() {
        // Add search bar
        searchBar = new SearchBar();
        searchBar.setPadding(new Insets(15, 15, 5, 15));
        searchBar.setOnAction(event -> performSearch());  // press Enter to search
        this.getChildren().add(searchBar);

        // Add filter component
        filterComponent = new FilterComponent();
        this.getChildren().add(filterComponent);

        // Add cook time selector
        cookTimeSelector = new CookTimeSelector();
        this.getChildren().add(cookTimeSelector);

        // Add ingredient search component
        ingredientSearchTab = new IngredientSearchTab();
        ingredientSearchTab.setPadding(new Insets(5, 15, 15, 15));
        this.getChildren().add(ingredientSearchTab);
    }

    private void showFindRecipesButton() {
        VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10, 0, 10, 0));

        Button findRecipesButton = new Button("Find Recipes!");
        findRecipesButton.getStyleClass().add("rainbow-button");
        findRecipesButton.setOnAction(event -> performSearch());

        buttonContainer.getChildren().add(findRecipesButton);
        this.getChildren().add(buttonContainer);
    }

    private void performSearch() {
        List<String> ingredients = ingredientSearchTab.getIngredients().stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .collect(Collectors.toList());

        int cookTime = cookTimeSelector.getCookTimeMinutes();
        String searchQuery = searchBar.getText();

        // Build the filter
        RecipeFilter filter = new RecipeFilter();

        if (!searchQuery.isEmpty()) {
            filter.setName(searchQuery);
        }

        if (!ingredients.isEmpty()) {
            filter.setIngredients(ingredients);
        }

        if (cookTime > 0) {
            filter.setMaxCookTime(cookTime);
        }

        // You can also add filter.setMaxPrepTime(...) if you have prep time input

        // Get filtered results
        List<Recipe> searchResults = recipeService.searchByFilter(filter);

        // Display results
        recipeGrid.displayRecipes(searchResults);
    }
}
