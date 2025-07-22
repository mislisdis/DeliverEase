package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Recipe.RecipeFilter;
import com.mycompany.dishcover.Recipe.RecipeService;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.UI.Component.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class MainPage extends VBox {

    private SearchBar searchBar;
    private FilterComponent filterComponent;
    private CookTimeSelector cookTimeSelector;
    private IngredientSearchTab ingredientSearchTab;
    private final RecipeGrid recipeGrid;
    private final RecipeService recipeService;

    public MainPage() {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("gradient-background");
        this.setPadding(new Insets(0, 0, 20, 0));
        this.setSpacing(10);

        recipeService = new RecipeService();

        showHeader();
        showSearchComponents();
        showFindRecipesButton();

        recipeGrid = new RecipeGrid();
        this.getChildren().add(recipeGrid);
        VBox.setVgrow(recipeGrid, Priority.ALWAYS);

        this.getChildren().add(new Footer());
    }

    private void showHeader() {
        this.getChildren().add(new HeaderCard());
    }

    private void showSearchComponents() {
        searchBar = new SearchBar();
        searchBar.setPadding(new Insets(15, 15, 5, 15));
        searchBar.setOnAction(event -> performSearch());
        this.getChildren().add(searchBar);

        filterComponent = new FilterComponent();
        this.getChildren().add(filterComponent);

        cookTimeSelector = new CookTimeSelector();
        this.getChildren().add(cookTimeSelector);

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
        String searchQuery = searchBar.getText();
        List<String> ingredients = ingredientSearchTab.getIngredients().stream()
                .filter(i -> !i.isEmpty())
                .collect(Collectors.toList());
        int cookTime = cookTimeSelector.getCookTimeMinutes();
        String difficulty = filterComponent.getDifficulty();
        boolean vegetarian = filterComponent.isVegetarianChecked();
        boolean vegan = filterComponent.isVeganChecked();

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

        List<Recipe> searchResults = recipeService.filterRecipes(filter);
        recipeGrid.displayRecipes(searchResults);
    }
}
