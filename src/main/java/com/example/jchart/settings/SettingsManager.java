/**
 * Class containing static methods that change the AppSettings object
 * AppSettings is the current STATE, the SettingsManager CHANGES current properties state
 */

package com.example.jchart.settings;

import java.io.*;
import java.util.Properties;

public class SettingsManager {

    private static final String propertyFilePath = "jchart_settings.properties" ;
    private static final double DEFAULT_DISTANCE = 3.0 ;  //the default distance to the chart, in meters
    private static final String DEFAULT_CHART_TYPE = "Sloan" ;

    /**
     * Save method for the app
     * @param settings - the AppSettings object
     */
    public static void save(AppSettings settings) {
        Properties properties = new Properties() ;
        properties.setProperty("distanceMeters", String.valueOf(settings.getDistanceMeters())) ;
        properties.setProperty("chartType", String.valueOf(settings.getChartType())) ;

        try (FileWriter writer = new FileWriter(propertyFilePath)) {
            properties.store(writer, "JChart Settings") ;
        } catch (IOException e) {
            System.err.println("Settings not saved successfully") ;
        }
    }

    /**
     * Method to load the current properties file
     */
    public static AppSettings load() {
        File file = new File(propertyFilePath) ;
        if (!file.exists()) {
            return new AppSettings(DEFAULT_DISTANCE, DEFAULT_CHART_TYPE) ;
        }

        Properties properties = new Properties() ;
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader) ;

            double distanceMeters ; //distance parameter, in a text box so we want to check inputs
            try {
                distanceMeters = Double.parseDouble(properties.getProperty("distanceMeters", String.valueOf(DEFAULT_DISTANCE))) ;
            } catch(NumberFormatException e) {
                distanceMeters = DEFAULT_DISTANCE ;
            }

            String chartType = properties.getProperty("chartType", DEFAULT_CHART_TYPE) ;

            return new AppSettings(distanceMeters, chartType) ;  //create the AppSettings object to be used
        } catch (IOException e) {
            System.err.println("Failed to load the settings") ;
            return new AppSettings(DEFAULT_DISTANCE, DEFAULT_CHART_TYPE) ;
        }
    }

}
