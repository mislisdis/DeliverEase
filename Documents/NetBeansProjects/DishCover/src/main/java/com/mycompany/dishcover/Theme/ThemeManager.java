
package com.mycompany.dishcover.Theme;


import com.mycompany.dishcover.Session.AppSession;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThemeManager {

    private static final String LIGHT_STYLE = "/com/hci/javafx/styles.css";
    private static final String DARK_STYLE = "/com/hci/javafx/dark-styles.css";

    private static ThemeManager instance;
    private final BooleanProperty brightModeProperty;
    private final List<Parent> registeredComponents;

    private ThemeManager() {
        // Initialize the bright mode property from AppSession
        brightModeProperty = new SimpleBooleanProperty(AppSession.getInstance().isBrightMode());
        registeredComponents = new ArrayList<>();

        // Add a listener to update all components when the theme changes
        brightModeProperty.addListener((observable, oldValue, newValue) -> {
            // Update AppSession
            AppSession.getInstance().setBrightMode(newValue);

            // Update all registered components
            applyThemeToAllComponents();
        });
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
        component.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource(styleResource)).toExternalForm());
    }
}

