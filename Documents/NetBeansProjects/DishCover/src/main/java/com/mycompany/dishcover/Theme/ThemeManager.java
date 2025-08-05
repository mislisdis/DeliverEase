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
        brightModeProperty = new SimpleBooleanProperty(true);
        registeredComponents = new ArrayList<>();

        brightModeProperty.addListener((obs, oldVal, newVal) -> applyThemeToAllComponents());
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void registerComponent(Parent component) {
        if (!registeredComponents.contains(component)) {
            registeredComponents.add(component);
            applyThemeToComponent(component);
        }
    }

    public void unregisterComponent(Parent component) {
        registeredComponents.remove(component);
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

    private void applyThemeToComponent(Parent component) {
        String styleResource = brightModeProperty.get() ? LIGHT_STYLE : DARK_STYLE;
        component.getStylesheets().clear();
        component.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(styleResource)).toExternalForm()
        );
    }

    // ✅ NEW METHODS
    public void applySavedTheme(Scene scene) {
        String styleResource = isBrightMode() ? LIGHT_STYLE : DARK_STYLE;
        scene.getStylesheets().add(
            Objects.requireNonNull(getClass().getResource(styleResource)).toExternalForm()
        );
    }

    public void toggleTheme(Scene scene) {
        toggleTheme();  // flip value
        scene.getStylesheets().clear(); // clear old
        applySavedTheme(scene); // add new
    }
}
