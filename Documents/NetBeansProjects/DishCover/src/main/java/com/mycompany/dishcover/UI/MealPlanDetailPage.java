package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Recipe.MealPlan;
import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.UI.Component.Footer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;

public class MealPlanDetailPage extends BorderPane {

    public MealPlanDetailPage(List<MealPlan> planList, Runnable backAction) {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("mealplan-container");
        this.setPadding(new Insets(0));

        VBox content = new VBox(30); // Original spacing
        content.setPadding(new Insets(30)); // Original padding
        content.getStyleClass().add("mealplan-container");

        // 🔙 Back Button
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back");
        backButton.getStyleClass().add("minor-button");
        backButton.setOnAction(e -> backAction.run());

        topBar.getChildren().add(backButton);
        content.getChildren().add(topBar);

        // 📅 Title
        Label title = new Label("📅 Meal Plan Details");
        title.getStyleClass().add("section-title");
        content.getChildren().add(title);

        // 📦 Plan Content
        for (MealPlan plan : planList) {
            VBox dayBox = new VBox(10);
            dayBox.setPadding(new Insets(20));
            dayBox.getStyleClass().add("mealplan-box");

            Label dayLabel = new Label("📋 " + plan.getDayOfWeek());
            dayLabel.getStyleClass().add("section-title");
            dayBox.getChildren().add(dayLabel);

            if (plan.getBreakfast() != null)
                dayBox.getChildren().add(createMealRow("🍳 Breakfast", plan.getBreakfast()));
            if (plan.getLunch() != null)
                dayBox.getChildren().add(createMealRow("🍲 Lunch", plan.getLunch()));
            if (plan.getDinner() != null)
                dayBox.getChildren().add(createMealRow("🍝 Dinner", plan.getDinner()));

            content.getChildren().add(dayBox);
        }

        // Footer
        content.getChildren().add(new Footer());

        // ✅ Scroll Setup with bigger viewport
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPrefViewportHeight(1600); // Increased for weekly view

        this.setCenter(scrollPane);
    }

    private HBox createMealRow(String mealType, Recipe recipe) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label mealLabel = new Label(mealType + ": " + recipe.getName());
        Button viewButton = new Button("View");
        viewButton.getStyleClass().add("minor-button");
        viewButton.setOnAction(e -> MainApplication.getInstance().showRecipeDisplay(recipe, "savedPlans"));

        row.getChildren().addAll(mealLabel, viewButton);
        return row;
    }
}
