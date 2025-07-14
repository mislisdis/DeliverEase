
package com.mycompany.dishcover.UI.Component;



import com.mycompany.dishcover.Theme.ThemeManager;
import javafx.scene.control.TextField;

public class SearchBar extends TextField {

    public SearchBar() {
        ThemeManager.getInstance().registerComponent(this);
        this.getStyleClass().add("search-bar");
        this.setPromptText("Search");
    }
}