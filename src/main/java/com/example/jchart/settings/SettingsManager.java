/**
 * Class containing static methods that change the AppSettings
 * AppSettings is the current STATE, the SettingsManager CHANGES current state
 */

package com.example.jchart;

import java.io.*;
import java.util.Properties;

public class SettingsManager {

    private static String propertyFilePath = "jchart_settings.properties" ;

    public static void save(AppSettings settings) {
        Properties properties = new Properties() ;
        properties.setProperty("pixelCount", String.valueOf(settings.getPixelCount())) ;
        properties.setProperty("distanceMeters", String.valueOf(settings.getDistanceMeters())) ;

        try(FileWriter writer = new FileWriter(propertyFilePath)) {
            properties.store(writer, "JChart Settings") ;
        } catch (IOException e) {
            System.out.println("Settings not saved successfully") ;
        }
    }

    /**
     * To load the properties file
     */
    public static AppSettings load() {
        File file = new File(propertyFilePath) ;
        if (!file.exists()) {
            return null ;  //first boot - save() hasn't been called yet
        }
        Properties properties = new Properties() ;
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader) ;
            double pixelCount = Double.parseDouble(properties.getProperty("pixelCount")) ;
            double distanceMeters = Double.parseDouble(properties.getProperty("distanceMeters")) ;
            return new AppSettings(pixelCount,distanceMeters) ;
        } catch (IOException e) {
            System.err.println("Failed to load the settings") ;
            return null ;
        }
    }

}
