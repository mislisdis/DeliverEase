package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Recipe.MealPlan;
import com.mycompany.dishcover.Recipe.Recipe;
import com.mycompany.dishcover.Theme.ThemeManager;
import com.mycompany.dishcover.UI.Component.RecipeCard;
import com.mycompany.dishcover.Util.ApiService;
import com.mycompany.dishcover.Util.MealPlanGenerator;
import com.mycompany.dishcover.Util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MealPlanPage extends BorderPane {

    private VBox mealPlanContainer;
    private ComboBox<String> planDurationBox;
    private CheckBox vegetarianCheck;
    private CheckBox veganCheck;
    private Button generateBtn;
    private Button saveBtn;
    private TextField planNameField;

    private List<MealPlan> mealPlans = new ArrayList<>();

    public MealPlanPage() {
        ThemeManager.getInstance().registerComponent(this);
        this.setPadding(new Insets(20));

        // --- Top Controls ---
        HBox topControls = new HBox(15);
        topControls.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back");
        backButton.setOnAction(e -> MainApplication.getInstance().showMainPage());

        planDurationBox = new ComboBox<>();
        planDurationBox.getItems().addAll("Daily", "3-Day", "Weekly");
        planDurationBox.setValue("Daily");

        vegetarianCheck = new CheckBox("Vegetarian");
        veganCheck = new CheckBox("Vegan");

        planNameField = new TextField();
        planNameField.setPromptText("Enter Plan Name");

        generateBtn = new Button("Generate Plan");
        saveBtn = new Button("Save Plan");

        Button viewSavedBtn = new Button("View Saved Plans");
        viewSavedBtn.setOnAction(e -> MainApplication.getInstance().showSavedMealPlansPage());

        topControls.getChildren().addAll(
                backButton,
                new Label("Plan:"), planDurationBox,
                vegetarianCheck, veganCheck,
                planNameField,
                generateBtn, saveBtn, viewSavedBtn
        );

        this.setTop(topControls);

        // --- Meal Plan Content ---
        mealPlanContainer = new VBox(20);
        mealPlanContainer.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(mealPlanContainer);
        scrollPane.setFitToWidth(true);
        this.setCenter(scrollPane);

        updateMealPlanUI(1); // initial view

        // --- Actions ---
        planDurationBox.setOnAction(e -> {
            int days = getSelectedDays();
            updateMealPlanUI(days);
        });

        generateBtn.setOnAction(e -> {
            int days = getSelectedDays();

            List<Recipe> all = ApiService.getAllRecipes();
            boolean veg = vegetarianCheck.isSelected();
            boolean vegan = veganCheck.isSelected();

            mealPlans = MealPlanGenerator.generate(days, all, veg, vegan);
            updateMealPlanUI(days);
        });

        saveBtn.setOnAction(e -> {
            int userId = Session.getUserId();
            String planName = planNameField.getText().trim();
            if (planName.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a name for the meal plan.");
                alert.showAndWait();
                return;
            }
            ApiService.saveMealPlanToDatabase(mealPlans, userId, planName);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Meal plan saved successfully!");
            alert.showAndWait();
        });
    }

    private int getSelectedDays() {
        return switch (planDurationBox.getValue()) {
            case "3-Day" -> 3;
            case "Weekly" -> 7;
            default -> 1;
        };
    }

    private void updateMealPlanUI(int numberOfDays) {
        mealPlanContainer.getChildren().clear();

        String[] dayNames = switch (numberOfDays) {
            case 3 -> new String[]{"Monday", "Tuesday", "Wednesday"};
            case 7 -> new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
            default -> new String[]{"Today"};
        };

        if (mealPlans.isEmpty() || mealPlans.size() != numberOfDays) {
            mealPlans.clear();
            for (String day : dayNames) {
                mealPlans.add(new MealPlan(day));
            }
        }

        for (MealPlan plan : mealPlans) {
            VBox dayBox = new VBox(10);
            dayBox.setPadding(new Insets(10));
            dayBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: #f9f9f9;");

            Text dayTitle = new Text(plan.getDayOfWeek());
            dayTitle.setFont(Font.font(18));

            HBox mealsRow = new HBox(20);
            mealsRow.setAlignment(Pos.CENTER_LEFT);

            mealsRow.getChildren().addAll(
                    createMealSlot("Breakfast", plan),
                    createMealSlot("Lunch", plan),
                    createMealSlot("Dinner", plan)
            );

            dayBox.getChildren().addAll(dayTitle, mealsRow);
            mealPlanContainer.getChildren().add(dayBox);
        }
    }

    private VBox createMealSlot(String mealType, MealPlan plan) {
        VBox mealSlot = new VBox(5);
        mealSlot.setAlignment(Pos.CENTER);
        mealSlot.setPrefWidth(200);

        Label label = new Label(mealType);
        label.setFont(Font.font(14));

        StackPane cardHolder = new StackPane();
        cardHolder.setPrefHeight(160);
        cardHolder.setPrefWidth(180);
        cardHolder.setStyle("-fx-border-color: gray; -fx-border-radius: 8; -fx-background-radius: 8; -fx-background-color: white;");

        Recipe recipe = switch (mealType) {
            case "Breakfast" -> plan.getBreakfast();
            case "Lunch" -> plan.getLunch();
            case "Dinner" -> plan.getDinner();
            default -> null;
        };

        if (recipe != null) {
            RecipeCard recipeCard = new RecipeCard(recipe);
            cardHolder.getChildren().add(recipeCard);
            cardHolder.setOnMouseClicked(e -> MainApplication.getInstance().showRecipeDisplay(recipe, "mealPlan"));
        } else {
            Label empty = new Label("Click to choose recipe");
            empty.setStyle("-fx-text-fill: gray;");
            cardHolder.getChildren().add(empty);
            cardHolder.setOnMouseClicked(e -> showRecipeSelector(plan, mealType));
        }

        mealSlot.getChildren().addAll(label, cardHolder);
        return mealSlot;
    }

    private void showRecipeSelector(MealPlan plan, String mealType) {
        Dialog<Recipe> dialog = new Dialog<>();
        dialog.setTitle("Select Recipe for " + mealType);
        dialog.getDialogPane().setPrefWidth(550);

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));

        TextField searchField = new TextField();
        searchField.setPromptText("Search recipes...");

        ListView<Recipe> listView = new ListView<>();
        List<Recipe> allRecipes = ApiService.getAllRecipes();
        listView.getItems().addAll(allRecipes);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe recipe, boolean empty) {
                super.updateItem(recipe, empty);
                setText((recipe == null || empty) ? "" : recipe.getName());
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Recipe> filtered = allRecipes.stream()
                    .filter(r -> r.getName().toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList());
            listView.getItems().setAll(filtered);
        });

        contentBox.getChildren().addAll(searchField, listView);
        dialog.getDialogPane().setContent(contentBox);

        ButtonType selectButton = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButton) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedRecipe -> {
            switch (mealType) {
                case "Breakfast" -> plan.setBreakfast(selectedRecipe);
                case "Lunch" -> plan.setLunch(selectedRecipe);
                case "Dinner" -> plan.setDinner(selectedRecipe);
            }
            updateMealPlanUI(mealPlans.size());
        });
    }
}
