module fi.metropolia.currency_converter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens fi.metropolia.currency_converter to javafx.fxml;
    exports fi.metropolia.currency_converter;
}