
package com.mycompany.dishcover.UI;


import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.UI.Component.Footer;
import com.mycompany.dishcover.UI.Component.HeaderCard;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

public class RecipeDisplay extends VBox {

    private final Recipe recipe;

    public RecipeDisplay(Recipe recipe) {
        this.recipe = recipe;

        // Register with theme manager
        ThemeManager.getInstance().registerComponent(this);

        // Set styling for the page
        this.getStyleClass().add("recipe-display");
        this.setPadding(new Insets(20));
        this.setSpacing(15);

        // Create and add components
        createHeader();
        createDetailsSection();
        createIngredientsSection();
        createInstructionsSection();
        createBackButton();

        // Footer
        Footer ft = new Footer();
        this.getChildren().add(ft);
    }

    private void createHeader() {
        // app Header
        HeaderCard hdc = new HeaderCard();
        ThemeManager.getInstance().registerComponent(hdc);
        this.getChildren().add(hdc);

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(15);
        headerBox.getStyleClass().add("recipe-header");

        // Recipe image
        ImageView recipeImage = new ImageView();
        // recipe has an image, set it here
        try {
            String imagePath = "/com/hci/javafx" + recipe.getImage_path();
            System.out.println(imagePath);
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)
            ));
            recipeImage.setImage(image);
        } catch (Exception e) {
            System.err.println(e);
        }

        recipeImage.setFitWidth(150);
        recipeImage.setFitHeight(150);
        recipeImage.getStyleClass().add("recipe-image");

        // Recipe title and basic info
        VBox titleBox = new VBox();
        titleBox.setSpacing(5);

        Label titleLabel = new Label(recipe.getName());
        titleLabel.getStyleClass().add("recipe-title");

        Label prepTimeLabel = new Label("Prep Time: " + recipe.getPrep_time() + " minutes");
        prepTimeLabel.getStyleClass().add("recipe-prep-time");

        Label difficultyLabel = new Label("Difficulty: " + recipe.getDifficulty());
        difficultyLabel.getStyleClass().add("recipe-difficulty");

        titleBox.getChildren().addAll(titleLabel, prepTimeLabel, difficultyLabel);

        if (recipe.isVegetarian()) {
            Label vegetarianLabel = new Label("Vegetarian ✅");
            vegetarianLabel.getStyleClass().add("recipe-difficulty");
            titleBox.getChildren().addAll(vegetarianLabel);
        }

        headerBox.getChildren().addAll(recipeImage, titleBox);
        this.getChildren().add(headerBox);
    }

    private void createDetailsSection() {
        VBox detailsBox = new VBox();
        detailsBox.setSpacing(10);
        detailsBox.getStyleClass().add("recipe-details-section");

        Label descriptionLabel = new Label("Description");
        descriptionLabel.getStyleClass().add("section-title");

        Label descriptionText = new Label("Coming soon...");
        descriptionText.getStyleClass().add("recipe-description");
        descriptionText.setWrapText(true);

        // Add serving size, calories, etc. if available
        HBox nutritionBox = new HBox();
        nutritionBox.setSpacing(15);
        nutritionBox.getStyleClass().add("nutrition-box");

        Label servingsLabel = new Label("Servings: 1");
        servingsLabel.getStyleClass().add("nutrition-item");

        Label caloriesLabel = new Label("Calories: " + recipe.getCalories() + " kcal");
        caloriesLabel.getStyleClass().add("nutrition-item");

        nutritionBox.getChildren().addAll(servingsLabel, caloriesLabel);

        detailsBox.getChildren().addAll(descriptionLabel, descriptionText, nutritionBox);
        this.getChildren().add(detailsBox);
    }

    private void createIngredientsSection() {
        VBox ingredientsBox = new VBox();
        ingredientsBox.setSpacing(10);
        ingredientsBox.getStyleClass().add("ingredients-section");

        Label ingredientsTitle = new Label("Ingredients");
        ingredientsTitle.getStyleClass().add("section-title");

        // Create a flow pane for ingredients
        FlowPane ingredientsPane = new FlowPane();
        ingredientsPane.setHgap(10);
        ingredientsPane.setVgap(10);
        ingredientsPane.setPrefWrapLength(600);

        List<String> ingredients = recipe.getIngredients();
        for (String ingredient : ingredients) {
            Label ingredientLabel = new Label(ingredient);
            ingredientLabel.getStyleClass().add("ingredient-item");
            ingredientsPane.getChildren().add(ingredientLabel);
        }

        ingredientsBox.getChildren().addAll(ingredientsTitle, ingredientsPane);
        this.getChildren().add(ingredientsBox);
    }

    private void createInstructionsSection() {
        VBox instructionsBox = new VBox();
        instructionsBox.setSpacing(10);
        instructionsBox.getStyleClass().add("instructions-section");

        Label instructionsTitle = new Label("Instructions");
        instructionsTitle.getStyleClass().add("section-title");

        VBox stepsBox = new VBox();
        stepsBox.setSpacing(10);

        List<String> steps = recipe.getSteps();
        for (int i = 0; i < steps.size(); i++) {
            HBox stepBox = new HBox();
            stepBox.setSpacing(10);
            stepBox.setAlignment(Pos.TOP_LEFT);

            Label stepNumberLabel = new Label((i + 1) + ".");
            stepNumberLabel.getStyleClass().add("step-number");

            Label stepTextLabel = new Label(steps.get(i));
            stepTextLabel.getStyleClass().add("step-text");
            stepTextLabel.setWrapText(true);
            HBox.setHgrow(stepTextLabel, Priority.ALWAYS);

            stepBox.getChildren().addAll(stepNumberLabel, stepTextLabel);
            stepsBox.getChildren().add(stepBox);
        }

        instructionsBox.getChildren().addAll(instructionsTitle, stepsBox);
        this.getChildren().add(instructionsBox);
    }

    private void createBackButton() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        Button backButton = new Button("Back to Recipes");
        backButton.getStyleClass().add("minor-button");
        backButton.setOnAction(event -> {
            // Go back to the main page with recipe list
            com.hci.javafx.MainApplication.getInstance().showMainPage();
        });

        buttonBox.getChildren().add(backButton);
        this.getChildren().add(buttonBox);
    }
}