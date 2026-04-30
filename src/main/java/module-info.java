module com.example.jchart {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.jchart to javafx.fxml;
    exports com.example.jchart;
}