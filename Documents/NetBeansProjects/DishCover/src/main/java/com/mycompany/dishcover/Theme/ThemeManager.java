package com.mycompany.dishcover.Theme;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeManager {

    private static final String LIGHT_STYLE = "/CSS/styles.css";
    private static final String DARK_STYLE = "/CSS/dark-styles.css";

    private static ThemeManager instance;

    private final BooleanProperty brightModeProperty;
    private final List<Parent> registeredComponents;
    private final List<Scene> registeredScenes;

    private ThemeManager() {
        brightModeProperty = new SimpleBooleanProperty(true); // Default to light theme
        registeredComponents = new ArrayList<>();
        registeredScenes = new ArrayList<>();

        brightModeProperty.addListener((obs, oldVal, newVal) -> {
            applyThemeToAllComponents();
            applyThemeToAllScenes();
        });
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    // Register UI containers like VBox/HBox
    public void registerComponent(Parent component) {
        if (!registeredComponents.contains(component)) {
            registeredComponents.add(component);
            applyThemeToComponent(component);
        }
    }

    public void unregisterComponent(Parent component) {
        registeredComponents.remove(component);
    }

    // Register entire Scene objects for full theme support
    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            applyThemeToScene(scene);
        }
    }

    public void unregisterScene(Scene scene) {
        registeredScenes.remove(scene);
    }

    public void toggleTheme() {
        brightModeProperty.set(!brightModeProperty.get());
    }

    public boolean isBrightMode() {
        return brightModeProperty.get();
    }

    public BooleanProperty brightModeProperty() {
        return brightModeProperty;
    }

    private void applyThemeToAllComponents() {
        for (Parent component : registeredComponents) {
            applyThemeToComponent(component);
        }
    }

    private void applyThemeToAllScenes() {
        for (Scene scene : registeredScenes) {
            applyThemeToScene(scene);
        }
    }

    private void applyThemeToComponent(Parent component) {
        String styleResource = brightModeProperty.get() ? LIGHT_STYLE : DARK_STYLE;
        component.getStylesheets().clear();
        component.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(styleResource)).toExternalForm()
        );
    }

    private void applyThemeToScene(Scene scene) {
        String styleResource = brightModeProperty.get() ? LIGHT_STYLE : DARK_STYLE;
        scene.getStylesheets().clear();
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(styleResource)).toExternalForm()
        );
    }

    // Use during app startup or scene switch
    public void applySavedTheme(Scene scene) {
        applyThemeToScene(scene);
        registerScene(scene); // Ensure future toggles affect it
    }

    // Toggle and apply to one scene
    public void toggleTheme(Scene scene) {
        toggleTheme();
        applyThemeToScene(scene);
    }
}
