module fi.metropolia.dictionary {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens fi.metropolia.dictionary to javafx.fxml;
    exports fi.metropolia.dictionary;
}