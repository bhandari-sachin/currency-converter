module fi.metropolia.currency_converter {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics; // optional, but safe to include

    // JPA / Hibernate
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;

    // Logging
    requires org.slf4j;

    // Open packages for reflection
    opens fi.metropolia.currency_converter.model to org.hibernate.orm.core;
    opens fi.metropolia.currency_converter to javafx.fxml;
    opens fi.metropolia.currency_converter.controller to javafx.fxml;

    // Exported packages
    exports fi.metropolia.currency_converter;
    exports fi.metropolia.currency_converter.model;
    exports fi.metropolia.currency_converter.controller;
    exports fi.metropolia.currency_converter.view;
    exports fi.metropolia.currency_converter.dao;
}
