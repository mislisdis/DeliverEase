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
import javafx.scene.layout.*;

import java.util.List;

public class RecipeDisplay extends VBox {

    private final Recipe recipe;

    public RecipeDisplay(Recipe recipe) {
        this.recipe = recipe;

        ThemeManager.getInstance().registerComponent(this);

        this.getStyleClass().add("recipe-display");
        this.setPadding(new Insets(20));
        this.setSpacing(15);

        createHeader();
        createDetailsSection();
        createIngredientsSection();
        createInstructionsSection();
        createBackButton();

        Footer ft = new Footer();
        this.getChildren().add(ft);
    }

    private void createHeader() {
        HeaderCard hdc = new HeaderCard();
        ThemeManager.getInstance().registerComponent(hdc);
        this.getChildren().add(hdc);

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(15);
        headerBox.getStyleClass().add("recipe-header");

        ImageView recipeImage = new ImageView();
        try {
            String imagePath = "/images/recipes/" + recipe.getImage_path();
            var imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                recipeImage.setImage(new Image(imageStream));
            } else {
                System.err.println("Image not found: " + imagePath);
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        recipeImage.setFitWidth(150);
        recipeImage.setFitHeight(150);
        recipeImage.getStyleClass().add("recipe-image");

        VBox titleBox = new VBox();
        titleBox.setSpacing(5);

        Label titleLabel = new Label(recipe.getName());
        titleLabel.getStyleClass().add("recipe-title");

        Label prepTimeLabel = new Label("Prep Time: " + recipe.getPrep_time());
        prepTimeLabel.getStyleClass().add("recipe-prep-time");

        // 🔧 Robust cook time icon loading
        ImageView clockIcon = new ImageView();
        try {
            var iconStream = getClass().getResourceAsStream("/images/icons/time.png");
            if (iconStream != null) {
                clockIcon.setImage(new Image(iconStream));
                clockIcon.setFitWidth(16);
                clockIcon.setFitHeight(16);
            } else {
                System.err.println("⚠️ time.png not found in /images/icons/");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error loading time.png: " + e.getMessage());
        }

        Label cookTimeLabel = new Label("Cook Time: " + recipe.getCook_time());
        cookTimeLabel.getStyleClass().add("recipe-prep-time");

        HBox cookTimeBox = new HBox(5, clockIcon, cookTimeLabel);
        cookTimeBox.setAlignment(Pos.CENTER_LEFT);

        // Difficulty tag
        Label difficultyLabel = new Label(recipe.getDifficulty());
        difficultyLabel.getStyleClass().add("recipe-tag");

        // Dietary tags
        if (recipe.isVegetarian()) {
            Label vegetarianLabel = new Label("🥗 Vegetarian");
            vegetarianLabel.getStyleClass().add("recipe-tag");
            titleBox.getChildren().add(vegetarianLabel);
        }

        if (recipe.isVegan()) {
            Label veganLabel = new Label("🌱 Vegan");
            veganLabel.getStyleClass().add("recipe-tag");
            titleBox.getChildren().add(veganLabel);
        }

        titleBox.getChildren().addAll(titleLabel, prepTimeLabel, cookTimeBox, difficultyLabel);
        headerBox.getChildren().addAll(recipeImage, titleBox);
        this.getChildren().add(headerBox);
    }

    private void createDetailsSection() {
        VBox detailsBox = new VBox();
        detailsBox.setSpacing(10);
        detailsBox.getStyleClass().add("recipe-details-section");

        Label descriptionLabel = new Label("Description");
        descriptionLabel.getStyleClass().add("section-title");

        Label descriptionText = new Label(recipe.getDescription());
        descriptionText.getStyleClass().add("recipe-description");
        descriptionText.setWrapText(true);

        HBox nutritionBox = new HBox();
        nutritionBox.setSpacing(15);
        nutritionBox.getStyleClass().add("nutrition-box");

        Label servingsLabel = new Label("Servings: " + recipe.getServings());
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

        FlowPane ingredientsPane = new FlowPane();
        ingredientsPane.setHgap(10);
        ingredientsPane.setVgap(10);
        ingredientsPane.setPrefWrapLength(600);

        for (String ingredient : recipe.getIngredients()) {
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
            com.mycompany.dishcover.MainApplication.getInstance().showMainPage();
        });

        buttonBox.getChildren().add(backButton);
        this.getChildren().add(buttonBox);
    }
}
