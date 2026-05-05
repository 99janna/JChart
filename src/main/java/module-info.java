module io.github.janna99.jchart {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jdk.compiler;
    requires javafx.graphics;

    opens io.github.janna99.jchart to javafx.fxml;
    exports io.github.janna99.jchart;
    exports io.github.janna99.jchart.settings;
    opens io.github.janna99.jchart.settings to javafx.fxml;
    exports io.github.janna99.jchart.ui;
    opens io.github.janna99.jchart.ui to javafx.fxml;
}