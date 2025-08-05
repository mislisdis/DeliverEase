package com.mycompany.dishcover.UI.Component;

import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.Util.ApiService;
import com.mycompany.dishcover.Util.Session;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class RecipeCard extends StackPane {

    private final Recipe recipe;

    public RecipeCard(Recipe recipe) {
        this.recipe = recipe;

        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("recipe-card");

        this.setPrefSize(220, 220);
        this.setMaxSize(220, 220);
        this.setPadding(new Insets(5));

        createCardContent();
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    private void createCardContent() {
        ImageView recipeImageView = createRecipeImageView();
        VBox overlay = createOverlay();
        this.getChildren().addAll(recipeImageView, overlay);
    }

    private ImageView createRecipeImageView() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(false);

        try {
            String imagePath = "/images/recipes/" + recipe.getImage_path();
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Could not load recipe image: " + e.getMessage());
            try {
                Image defaultImage = new Image(Objects.requireNonNull(
                        getClass().getResourceAsStream("/images/recipes/default.jpg")));
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Could not load default image: " + ex.getMessage());
            }
        }

        Rectangle clip = new Rectangle(220, 220);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        return imageView;
    }

    private VBox createOverlay() {
        VBox overlay = new VBox(5);
        overlay.setAlignment(Pos.BOTTOM_LEFT);
        overlay.setPadding(new Insets(10));
        overlay.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.6), transparent);");

        Label nameLabel = new Label(recipe.getName());
        nameLabel.getStyleClass().add("recipe-card-title");
        nameLabel.setStyle("-fx-text-fill: white;");

        HBox tags = new HBox(5);
        tags.setAlignment(Pos.CENTER_LEFT);

        if (recipe.isVegan()) {
            Label veganTag = new Label("🌱 Vegan");
            veganTag.getStyleClass().add("recipe-tag");
            veganTag.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
            tags.getChildren().add(veganTag);
        }

        if (recipe.isVegetarian()) {
            Label vegTag = new Label("🥗 Vegetarian");
            vegTag.getStyleClass().add("recipe-tag");
            vegTag.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
            tags.getChildren().add(vegTag);
        }

        Button saveBtn = new Button("♥ Save");
        saveBtn.setStyle("-fx-font-size: 10px; -fx-background-color: #e74c3c; -fx-text-fill: white;");

        saveBtn.setOnAction(e -> {
    int userId = Session.getUserId();

    ApiService.saveRecipeToFavoritesAsync(recipe.getId(), userId).thenAccept(status -> {
        Platform.runLater(() -> {
            Alert alert;
            switch (status) {
                case "success" -> {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Recipe saved successfully!");
                    saveBtn.setDisable(true);
                }
                case "duplicate" -> {
                    alert = new Alert(Alert.AlertType.INFORMATION, "Recipe is already in your favorites.");
                    saveBtn.setDisable(true);
                }
                default -> alert = new Alert(Alert.AlertType.ERROR, "Failed to save recipe. Please try again.");
            }
            alert.showAndWait();
        });
    });
});


        overlay.getChildren().add(nameLabel);
        if (!tags.getChildren().isEmpty()) {
            overlay.getChildren().add(tags);
        }
        overlay.getChildren().add(saveBtn);

        return overlay;
    }
}
