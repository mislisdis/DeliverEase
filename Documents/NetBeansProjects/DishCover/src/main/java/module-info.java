module com.mycompany.dishcover {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.sql;


    // Needed for FXMLLoader to access controllers
    opens com.mycompany.dishcover to javafx.fxml;
    opens com.mycompany.dishcover.Recipe to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.mycompany.dishcover.UI to javafx.fxml;
    opens com.mycompany.dishcover.Theme to javafx.fxml;
    

    // Allow use in other parts of your project
    exports com.mycompany.dishcover;
    exports com.mycompany.dishcover.Recipe;
    exports com.mycompany.dishcover.UI;
    exports com.mycompany.dishcover.Theme;
    exports com.mycompany.dishcover.Util;
}
