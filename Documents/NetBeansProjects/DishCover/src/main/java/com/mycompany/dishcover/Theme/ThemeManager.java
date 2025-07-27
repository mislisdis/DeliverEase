package com.mycompany.dishcover.Theme;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeManager {

    // ✅ Resource-relative CSS paths
    private static final String LIGHT_STYLE = "/CSS/styles.css";
    private static final String DARK_STYLE = "/CSS/dark-styles.css";

    private static ThemeManager instance;

    private final BooleanProperty brightModeProperty;
    private final List<Parent> registeredComponents;

    private ThemeManager() {
        // ✅ Start in bright mode by default (you can toggle later)
        brightModeProperty = new SimpleBooleanProperty(true);
        registeredComponents = new ArrayList<>();

        // Update components when theme changes
        brightModeProperty.addListener((observable, oldValue, newValue) -> applyThemeToAllComponents());
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

        try {
            component.getStylesheets().clear();
            component.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource(styleResource),
                    "Stylesheet not found: " + styleResource
                ).toExternalForm()
            );
        } catch (NullPointerException e) {
            System.err.println("❌ Error applying theme: " + e.getMessage());
        }
    }
}
