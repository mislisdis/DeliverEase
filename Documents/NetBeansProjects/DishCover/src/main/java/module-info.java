module com.mycompany.dishcover {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires lombok;

    opens com.mycompany.dishcover to javafx.fxml;
    exports com.mycompany.dishcover;
}

