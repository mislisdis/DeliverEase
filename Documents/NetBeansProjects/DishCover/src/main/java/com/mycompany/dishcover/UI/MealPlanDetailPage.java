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
import javafx.scene.layout.*;

import java.util.List;

public class MealPlanDetailPage extends VBox {

    public MealPlanDetailPage(List<MealPlan> planList) {
        ThemeManager.getInstance().registerComponent(this);
        this.setPadding(new Insets(20));
        this.setSpacing(20);
        this.getStyleClass().add("meal-plan-detail-page");

        Label title = new Label("📅 Meal Plan Details");
        title.getStyleClass().add("section-title");
        this.getChildren().add(title);

        for (MealPlan plan : planList) {
            VBox dayBox = new VBox();
            dayBox.setSpacing(10);
            dayBox.setPadding(new Insets(10));
            dayBox.getStyleClass().add("meal-day-box");

            Label dayLabel = new Label("📋 " + plan.getDayOfWeek());
            dayLabel.getStyleClass().add("day-label");
            dayBox.getChildren().add(dayLabel);

            if (plan.getBreakfast() != null)
                dayBox.getChildren().add(createMealRow("🍳 Breakfast", plan.getBreakfast()));
            if (plan.getLunch() != null)
                dayBox.getChildren().add(createMealRow("🍲 Lunch", plan.getLunch()));
            if (plan.getDinner() != null)
                dayBox.getChildren().add(createMealRow("🍝 Dinner", plan.getDinner()));

            this.getChildren().add(dayBox);
        }

        createBackButton();
        this.getChildren().add(new Footer());
    }

    private HBox createMealRow(String mealType, Recipe recipe) {
        HBox row = new HBox();
        row.setSpacing(15);
        row.setAlignment(Pos.CENTER_LEFT);

        Label mealLabel = new Label(mealType + ": " + recipe.getName());
        Button viewBtn = new Button("View");
        viewBtn.getStyleClass().add("mini-button");
        viewBtn.setOnAction(e -> MainApplication.getInstance().showRecipeDisplay(recipe, "savedPlans"));

        row.getChildren().addAll(mealLabel, viewBtn);
        return row;
    }

    private void createBackButton() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button backButton = new Button("← Back to Saved Plans");
        backButton.getStyleClass().add("minor-button");
        backButton.setOnAction(e -> MainApplication.getInstance().showSavedMealPlansPage());

        buttonBox.getChildren().add(backButton);
        this.getChildren().add(buttonBox);
    }
}
