
package com.mycompany.dishcover.UI.Component;


import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;

public class FilterComponent extends VBox {

    public FilterComponent() {
        ThemeManager.getInstance().registerComponent(this);

        this.setSpacing(10);
        this.setPadding(new Insets(15, 15, 5, 15));

        createFilterHeader();
    }

    private void createFilterHeader() {
        // Create container for filter icon and text
        HBox filterHeader = new HBox();
        filterHeader.setAlignment(Pos.CENTER_LEFT);
        filterHeader.setSpacing(10);

        // Create filter icon (using a polygon to create a funnel shape)
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

        // Create filter text
        Label filterText = new Label("Filter");
        filterText.getStyleClass().add("filter-text");

        filterHeader.getChildren().addAll(filterIcon, filterText);
        this.getChildren().add(filterHeader);
    }
}