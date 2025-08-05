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

    private ThemeManager() {
        brightModeProperty = new SimpleBooleanProperty(true); // Default to light theme
        registeredComponents = new ArrayList<>();

        // Whenever the theme changes, re-apply it to all registered UI components
        brightModeProperty.addListener((obs, oldVal, newVal) -> applyThemeToAllComponents());
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    // Used to track reusable UI components like reusable containers
    public void registerComponent(Parent component) {
        if (!registeredComponents.contains(component)) {
            registeredComponents.add(component);
            applyThemeToComponent(component);
        }
    }

    public void unregisterComponent(Parent component) {
        registeredComponents.remove(component);
    }

    // Switch the theme boolean and trigger stylesheet changes
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

    private void applyThemeToComponent(Parent component) {
        String styleResource = brightModeProperty.get() ? LIGHT_STYLE : DARK_STYLE;
        component.getStylesheets().clear();
        component.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(styleResource)).toExternalForm()
        );
    }

    // ✅ Apply current theme to a Scene (e.g. during app start or screen transitions)
    public void applySavedTheme(Scene scene) {
        String styleResource = isBrightMode() ? LIGHT_STYLE : DARK_STYLE;
        scene.getStylesheets().clear();
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(styleResource)).toExternalForm()
        );
    }

    // ✅ Toggle and apply theme directly to a Scene
    public void toggleTheme(Scene scene) {
        toggleTheme();
        applySavedTheme(scene);
    }
}
