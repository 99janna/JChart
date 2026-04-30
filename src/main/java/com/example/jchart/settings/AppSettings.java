/**
 * Data representing the application's settings.
 * The pixelcount within the width of a credit card, and distance from eye to screen are
 * the only calibration variables we need.
 */

package com.example.jchart;

public class AppSettings {

    private double pixelCount ;
    private double distanceMeters ;

    /**
     * The constructor for the app settings object
     */
    public AppSettings(double pixelCount, double distanceMeters) {
        this.pixelCount = pixelCount ;
        this.distanceMeters = distanceMeters ;
    }

    public double getPixelCount() {
        return pixelCount ;
    }
    public double getDistanceMeters() {
        return distanceMeters ;
    }
    public void setPixelCount(double pixelCount) {
        this.pixelCount = pixelCount ;
    }
    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters ;
    }
}
