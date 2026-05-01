package com.example.jchart;

import com.example.jchart.settings.AppSettings;
import com.example.jchart.settings.SettingsManager;
import com.example.jchart.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class JChartApplication extends Application {
    @Override
    public void start(Stage stage) {
        AppSettings settings = SettingsManager.load() ; //loads the existing settings
        new MainWindow(stage, settings) ; //creates the main window
    }
}
