package com.mycompany.dishcover.UI;

import com.mycompany.dishcover.MainApplication;
import com.mycompany.dishcover.Util.ApiService;
import com.mycompany.dishcover.Util.Session;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;


public class SavedMealPlansPage extends BorderPane {

    public SavedMealPlansPage() {
        this.setPadding(new Insets(20));

        Button backBtn = new Button("← Back to Meal Plan Page");
        backBtn.setOnAction(e -> MainApplication.getInstance().showMealPlanPage());

        VBox listContainer = new VBox(15);
        listContainer.setPadding(new Insets(20));

        int userId = Session.getUserId();
        List<ApiService.SavedMealPlan> savedPlans = ApiService.getSavedMealPlans(userId);

        if (savedPlans.isEmpty()) {
            listContainer.getChildren().add(new Text("No saved meal plans found."));
        } else {
            for (ApiService.SavedMealPlan saved : savedPlans) {
                VBox card = new VBox(10);
                card.setPadding(new Insets(15));
                card.setStyle("-fx-border-color: #ccc; -fx-background-color: #fdfdfd; -fx-background-radius: 8; -fx-border-radius: 8;");

                Label name = new Label("Name: " + saved.getPlanName());
                name.setFont(Font.font(16));

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
                Label date = new Label("Saved: " + sdf.format(saved.getDateSaved()));

                HBox buttons = new HBox(10);
                Button viewBtn = new Button("View");
                Button renameBtn = new Button("Rename");
                Button deleteBtn = new Button("Delete");

                viewBtn.setOnAction(e -> MainApplication.getInstance().showMealPlanDetailPage(saved.getMealPlans()));

                renameBtn.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(saved.getPlanName());
                    dialog.setTitle("Rename Meal Plan");
                    dialog.setHeaderText(null);
                    dialog.setContentText("Enter new plan name:");
                    dialog.showAndWait().ifPresent(newName -> {
                        ApiService.renameMealPlan(saved.getPlanSetId(), newName);
                        MainApplication.getInstance().showSavedMealPlansPage(); // Refresh
                    });
                });

                deleteBtn.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this meal plan?");
                    confirm.showAndWait().ifPresent(result -> {
                        if (result.getButtonData().isDefaultButton()) {
                            ApiService.deleteSavedMealPlan(saved.getPlanSetId());
                            MainApplication.getInstance().showSavedMealPlansPage(); // Refresh
                        }
                    });
                });

                buttons.getChildren().addAll(viewBtn, renameBtn, deleteBtn);

                card.getChildren().addAll(name, date, buttons);
                listContainer.getChildren().add(card);
            }
        }

        this.setTop(backBtn);
        this.setCenter(new ScrollPane(listContainer));
    }
}
