package com.mycompany.dishcover.UI.Component;

import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class IngredientSearchTab extends VBox {

    private final VBox inputContainer = new VBox(10);

    public IngredientSearchTab() {
        ThemeManager.getInstance().registerComponent(this);

        Label title = new Label("Ingredients");
        title.getStyleClass().add("ingredients-text");

        this.getChildren().add(title);
        this.getChildren().add(inputContainer);

        addIngredientField();
        addIngredientField();

        Button addButton = new Button("+ Add Ingredient");
        addButton.setOnAction(e -> addIngredientField());
        addButton.getStyleClass().add("rainbow-button"); // Optional, matches theme

        this.getChildren().add(addButton);
        VBox.setMargin(addButton, new Insets(10, 15, 0, 15));
    }

    private void addIngredientField() {
        TextField field = new TextField();
        field.setPromptText("e.g. butter");
        field.getStyleClass().add("ingredient-input");
        VBox.setMargin(field, new Insets(10, 10, 10, 10));
        inputContainer.getChildren().add(field);
    }

    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        for (var node : inputContainer.getChildren()) {
            if (node instanceof TextField field) {
                String text = field.getText().trim();
                if (!text.isEmpty()) ingredients.add(text);
            }
        }
        return ingredients;
    }
}
