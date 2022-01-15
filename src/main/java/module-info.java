module com.example.lab_gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires junit;
    requires org.apache.pdfbox;
    requires javafx.web;

    opens com.example.lab_gui to javafx.fxml;
    exports com.example.lab_gui;
}