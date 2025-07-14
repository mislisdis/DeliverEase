
package com.mycompany.dishcover.UI.Component;


import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

import java.util.Objects;

@Getter
public class RecipeCard extends StackPane {

    private Recipe recipe;

    public RecipeCard(Recipe recipe) {
        this.recipe = recipe;

        ThemeManager.getInstance().registerComponent(this);

        // Apply recipe-card style
        this.getStyleClass().add("recipe-card");

        // Set fixed dimensions for the card
        this.setPrefWidth(150);
        this.setPrefHeight(150);
        this.setMaxWidth(150);
        this.setMaxHeight(150);

        // Add padding to the card
        this.setPadding(new Insets(5));

        // Create the card content
        createCardContent();
    }

    private void createCardContent() {
        // Load recipe image
        ImageView recipeImageView = createRecipeImageView();

        // Create overlay for the recipe name
        VBox textOverlay = createTextOverlay();

        // Create non-visual indicator if present
        createNonVisualIndicator();

        // Add components to the StackPane
        this.getChildren().addAll(recipeImageView, textOverlay);
    }

    private ImageView createRecipeImageView() {
        ImageView recipeImageView = new ImageView();
        recipeImageView.setFitWidth(150);
        recipeImageView.setFitHeight(150);
        recipeImageView.setPreserveRatio(true); // Add this to maintain aspect ratio

        try {
            // Try to load the image from the path in the recipe
            String imagePath = "images/recipes" + recipe.getImage_path();

            Image recipeImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)));
            recipeImageView.setImage(recipeImage);

        } catch (Exception e) {
            System.err.println("Could not load recipe image: " + e.getMessage());
            // Load a default image if the recipe image is not available
            try {
                Image defaultImage = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/com/hci/javafx/image/recipes/default.jpg")));
                recipeImageView.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Could not load default image: " + ex.getMessage());
            }
        }

        // Apply rounded corners with the correct approach
        Rectangle clip = new Rectangle(150, 150);
        clip.setArcWidth(20);  // Control the corner radius
        clip.setArcHeight(20); // Control the corner radius
        recipeImageView.setClip(clip);

        return recipeImageView;
    }

    private VBox createTextOverlay() {
        VBox textOverlay = new VBox();
        textOverlay.setAlignment(Pos.BOTTOM_LEFT);
        textOverlay.setPadding(new Insets(10));

        // Create semi-transparent background for the text
        textOverlay.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.7), transparent);");

        // Create recipe name label
        Label nameLabel = new Label(recipe.getName());
        nameLabel.getStyleClass().add("recipe-card-title");
        nameLabel.setStyle("-fx-text-fill: white;");

        textOverlay.getChildren().add(nameLabel);
        return textOverlay;
    }

    private void createNonVisualIndicator() {
        // Create non-visual indicator at the bottom of the card
        VBox nonVisualBox = new VBox();
        nonVisualBox.setAlignment(Pos.BOTTOM_RIGHT);
        nonVisualBox.setPadding(new Insets(5));

        Label nonVisualLabel = new Label("Icon Visual");
        nonVisualLabel.getStyleClass().add("non-visual-text");
        nonVisualLabel.setStyle("-fx-text-fill: white;");

        Circle circle = new Circle(10);
        circle.setStyle("-fx-fill: white; -fx-opacity: 0.7;");

        StackPane iconContainer = new StackPane(circle, nonVisualLabel);
        iconContainer.setAlignment(Pos.CENTER);

        nonVisualBox.getChildren().add(iconContainer);
        this.getChildren().add(nonVisualBox);
    }

}
