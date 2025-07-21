package com.mycompany.dishcover.UI.Component;

import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;

public class FilterComponent extends VBox {

    private ComboBox<String> difficultyDropdown;
    private CheckBox vegetarianCheckBox;
    private CheckBox veganCheckBox;

    public FilterComponent() {
        ThemeManager.getInstance().registerComponent(this);

        this.setSpacing(10);
        this.setPadding(new Insets(15, 15, 5, 15));

        createFilterHeader();
        createDifficultySelector();
        createDietaryFilters();
    }

    private void createFilterHeader() {
        HBox filterHeader = new HBox();
        filterHeader.setAlignment(Pos.CENTER_LEFT);
        filterHeader.setSpacing(10);

        Polygon filterIcon = new Polygon();
        filterIcon.getPoints().addAll(
                0.0, 0.0,
                12.0, 0.0,
                12.0, 4.0,
                8.0, 8.0,
                8.0, 12.0,
                4.0, 14.0,
                4.0, 8.0,
                0.0, 4.0
        );
        filterIcon.getStyleClass().add("filter-icon");

        Label filterText = new Label("Filter");
        filterText.getStyleClass().add("filter-text");

        filterHeader.getChildren().addAll(filterIcon, filterText);
        this.getChildren().add(filterHeader);
    }

    private void createDifficultySelector() {
        Label label = new Label("Difficulty:");
        difficultyDropdown = new ComboBox<>();
        difficultyDropdown.getItems().addAll("", "Easy", "Medium", "Hard");
        difficultyDropdown.setValue(""); // Default to none selected

        VBox container = new VBox(5, label, difficultyDropdown);
        container.setPadding(new Insets(0, 0, 5, 0));
        this.getChildren().add(container);
    }

    private void createDietaryFilters() {
        Label label = new Label("Dietary Preferences:");

        vegetarianCheckBox = new CheckBox("Vegetarian");
        veganCheckBox = new CheckBox("Vegan");

        VBox container = new VBox(5, label, vegetarianCheckBox, veganCheckBox);
        container.setPadding(new Insets(0, 0, 5, 0));
        this.getChildren().add(container);
    }

    // ------------------------
    // Getters for MainPage
    // ------------------------

    public String getDifficulty() {
        String selected = difficultyDropdown.getValue();
        return selected == null || selected.isBlank() ? null : selected;
    }

    public Boolean isVegetarianChecked() {
        return vegetarianCheckBox.isSelected();
    }

    public Boolean isVeganChecked() {
        return veganCheckBox.isSelected();
    }
}
