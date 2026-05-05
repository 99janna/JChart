package io.github.janna99.jchart;

import io.github.janna99.jchart.settings.AppSettings;
import io.github.janna99.jchart.settings.SettingsManager;
import io.github.janna99.jchart.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class JChartApplication extends Application {
    @Override
    public void start(Stage stage) {
        AppSettings settings = SettingsManager.load() ; //loads the existing settings
        new MainWindow(stage, settings) ; //creates the main window
    }
}
