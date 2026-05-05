package io.github.janna99.jchart;
import java.lang.Math ;

/**
 * Class to calculate the exact sizes of the optotypes using distance and angle calculations
 * it will convert acuity → visual angle, and calculate the physical size of the rendered letters in each row
 * There will be one ArcMinutes object created for each row on the chart (because letters on the same row are the same size)
 */

public class ArcMinutes {

    protected double denominator ;
    protected double distanceMeters ;

    /**
     * Constructor for the ArcMinutes class
     * @param denominator in sloan fraction
     * @param distanceMeters from eye to chart
     */
    public ArcMinutes(double denominator, double distanceMeters) {
        this.denominator = denominator ;
        this.distanceMeters = distanceMeters ;
    }

    /**
     * This method converts the arcminute value for each potential value of visual acuity
     * @return the angle in radians for this specific optotype's angular size
     */
    public double angleInRadians() {
        double arcMinutes = 5.0 * (denominator / 20.0) ;
        double degrees = arcMinutes / 60.0 ;

        return Math.toRadians(degrees) ;
    }

    /**
     * This method will return the height for each desired visual acuity measurement
     * @return the height in meters
     */
    public double calculateHeightMeters() {
        double radians = angleInRadians() ;
        double height = 2 * distanceMeters * Math.tan(radians/2.0) ;

        return height ;
    }

}
