package com.mycompany.dishcover.UI.Component;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;

public class Footer extends HBox {

    public Footer() {
        ThemeManager.getInstance().registerComponent(this);

        Hyperlink aboutUs = new Hyperlink("About Us");
        aboutUs.getStyleClass().add("footer-link");
        aboutUs.setOnAction(event -> sendToGithub());

        Hyperlink feedback = new Hyperlink("Feedback");
        feedback.getStyleClass().add("footer-link");
        feedback.setOnAction(event -> sendToSurvey());

        Hyperlink policy = new Hyperlink("Privacy Policy");
        policy.getStyleClass().add("footer-link");
        policy.setOnAction(event -> System.out.println("Coming soon..."));

        this.getChildren().addAll(aboutUs, feedback, policy);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(40);
    }

    private void sendToSurvey() {
        openWebPage("https://docs.google.com/forms/...");
    }

    private void sendToGithub() {
        openWebPage("https://github.com/mislisdis/DishCovery");
    }

    private void openWebPage(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.err.println("Desktop is not supported");
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL: " + url);
        } catch (IOException e) {
            System.err.println("Failed to open URL: " + e.getMessage());
        }
    }
}
