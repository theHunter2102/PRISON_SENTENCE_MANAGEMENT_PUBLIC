module com.example.psmsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires MaterialFX;
    requires java.sql.rowset;
    requires jdk.compiler;
    requires mysql.connector.j;
    requires org.apache.poi.ooxml;

    opens com.example.psmsystem to javafx.fxml;
    exports com.example.psmsystem;
    exports com.example.psmsystem.controller;
    opens com.example.psmsystem.controller to javafx.fxml;
    opens com.example.psmsystem.model.prisoner to javafx.base;
    exports com.example.psmsystem.controller.ManagementVisit;
    opens com.example.psmsystem.controller.ManagementVisit to javafx.fxml;

    exports com.example.psmsystem.controller.prisoner;
    opens com.example.psmsystem.controller.prisoner;
    opens com.example.psmsystem.model.managementvisit to javafx.base;

    exports com.example.psmsystem.controller.health;
    opens com.example.psmsystem.controller.health to javafx.fxml;
    opens com.example.psmsystem.model.health to javafx.base;

    exports com.example.psmsystem.controller.sentence;
    opens com.example.psmsystem.controller.sentence to javafx.fxml;
    opens com.example.psmsystem.model.sentence to javafx.base;

    exports com.example.psmsystem.controller.crime;
    opens com.example.psmsystem.controller.crime to javafx.fxml;
    opens com.example.psmsystem.model.crime to javafx.base;

    exports com.example.psmsystem.controller.assess;
    opens com.example.psmsystem.controller.assess to javafx.fxml;
    opens com.example.psmsystem.model.assess to javafx.base;

    exports com.example.psmsystem.controller.report;
    opens com.example.psmsystem.controller.report to javafx.fxml;
    opens com.example.psmsystem.model.report to javafx.base;

    exports com.example.psmsystem.controller.userlog;
    opens com.example.psmsystem.controller.userlog to javafx.fxml;
    opens com.example.psmsystem.model.userlog to javafx.base;
}
