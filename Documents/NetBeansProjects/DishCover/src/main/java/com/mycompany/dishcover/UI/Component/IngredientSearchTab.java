
package com.mycompany.dishcover.UI.Component;



import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class IngredientSearchTab extends VBox {

    TextField searchField1, searchField2;

    public IngredientSearchTab() {
        ThemeManager.getInstance().registerComponent(this);

        Label title = new Label("Ingredients");
        title.getStyleClass().add("ingredients-text");
        this.getChildren().add(title);

        searchField1 = new TextField();
        searchField1.getStyleClass().add("ingredient-input");
        searchField1.setPromptText("e.g eggs");
        this.getChildren().add(searchField1);

        searchField2 = new TextField();
        searchField2.getStyleClass().add("ingredient-input");
        searchField2.setPromptText("e.g butter");
        this.getChildren().add(searchField2);

        VBox.setMargin(searchField1, new Insets(10, 10, 10, 10));
        VBox.setMargin(searchField2, new Insets(10, 10, 10, 10));
    }

    public List<String> getIngredients() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(searchField1.getText());
        ingredients.add(searchField2.getText());
        return ingredients;
    }
}