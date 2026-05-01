/**
 * Data representing the application's settings.
 */

package com.example.jchart.settings;

public class AppSettings {

    private double distanceMeters ;
    private String chartType ;

    /**
     * The constructor for the app settings object
     * @param distanceMeters - distance setting in meters
     * @param chartType - String of the type of chart
     */
    public AppSettings(double distanceMeters, String chartType) {
        this.distanceMeters = distanceMeters ;
        this.chartType = chartType ;
    }

    /**
     * Getter and setter methods for the distance and chartType parameters for settings
     */
    public double getDistanceMeters() {
        return distanceMeters ;
    }
    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters ;
    }
    public String getChartType() {
        return chartType ;
    }
    public void setChartType(String chartType) {
        this.chartType = chartType ;
    }
}
