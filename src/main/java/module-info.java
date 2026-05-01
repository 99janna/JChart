module com.example.jchart {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.compiler;
    requires javafx.graphics;

    opens com.example.jchart to javafx.fxml;
    exports com.example.jchart;
    exports com.example.jchart.settings;
    opens com.example.jchart.settings to javafx.fxml;
    exports com.example.jchart.ui;
    opens com.example.jchart.ui to javafx.fxml;
}