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

        // Scrollable recipe grid
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        recipeGrid = new RecipeGrid();
        scrollPane.setContent(recipeGrid);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        this.getChildren().add(scrollPane);

        // Add footer
        Footer ft = new Footer();
        this.getChildren().add(ft);
    }

    private void showHeader() {
        HeaderCard headerCard = new HeaderCard();
        this.getChildren().add(headerCard);
    }

    private void showSearchComponents() {
        // Search bar
        searchBar = new SearchBar();
        searchBar.setPadding(new Insets(15, 15, 5, 15));
        searchBar.setOnAction(event -> performSearch());  // search on Enter key
        this.getChildren().add(searchBar);

        // Filter section
        filterComponent = new FilterComponent();
        this.getChildren().add(filterComponent);

        // Cook time slider/input
        cookTimeSelector = new CookTimeSelector();
        this.getChildren().add(cookTimeSelector);

        // Ingredient tab
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
        // Get input from UI
        String searchQuery = searchBar.getText();
        List<String> ingredients = ingredientSearchTab.getIngredients().stream()
                .filter(i -> !i.isEmpty())
                .collect(Collectors.toList());
        int cookTime = cookTimeSelector.getCookTimeMinutes();
        String difficulty = filterComponent.getDifficulty();
        boolean vegetarian = filterComponent.isVegetarianChecked();
        boolean vegan = filterComponent.isVeganChecked();

        // Build recipe filter
        RecipeFilter filter = new RecipeFilter();

        if (!searchQuery.isBlank()) {
            filter.setName(searchQuery);
        }

        if (!ingredients.isEmpty()) {
            filter.setIngredients(ingredients);
        }

        if (cookTime > 0) {
            filter.setMaxCookTime(cookTime);
        }

        if (difficulty != null && !difficulty.isBlank()) {
            filter.setDifficulty(difficulty);
        }

        filter.setVegetarian(vegetarian);
        filter.setVegan(vegan);

        System.out.println("🔍 Search Filter: " + filter);

        // Perform search
        List<Recipe> searchResults = recipeService.searchByFilter(filter);

        // Display search results
        recipeGrid.displayRecipes(searchResults);
    }
}
