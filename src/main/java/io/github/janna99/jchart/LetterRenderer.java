/**
 * Class that handles the rendering of individual letters
 * Loads the Sloan font from the resources folder
 * Calibrates height using the ArcMinutes
 * returns the Text with the correct font and point size
 */

package io.github.janna99.jchart;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LetterRenderer {

    private ArcMinutes arcMinutes;

    //static variables to load in the font
    private static final String SLOAN_FONT_PATH = "/fonts/Sloan.otf" ;
    public static final Font sloanFont = loadSloanFont();

    private double screenPPI ; //screen's pixel density

    /**
     * Constructor for the letter renderer
     * @param arcMinutes
     * @param screenPPI
     */
    public LetterRenderer(ArcMinutes arcMinutes, double screenPPI) {
        this.arcMinutes = arcMinutes ;
        this.screenPPI = screenPPI ;
    }

    /**
     * Load sloan font to be rendered on the chart
     * @return Sloan font for use
     */
    private static Font loadSloanFont() {
        //creates a font object, by loading the resource, overridden later
        Font font = Font.loadFont(LetterRenderer.class.getResourceAsStream(SLOAN_FONT_PATH), 10); //size overridden later

        if (font == null) {
            System.err.println("Sloan font not found, falling back to default");
            return Font.font(10) ;
        }
        return font ;
    }

    /**
     * Render a single letter (optotype) at the correct size, in Sloan font
     * @param letter: Character to display
     */
    public Text renderLetter(String letter, int contrastPercent) {
        double heightPixels = pixelHeightAfterCalibration(arcMinutes) ; //size of the object in points
        Text text = new Text(letter); // Create JavaFX Text node
        text.setFont(Font.font(sloanFont.getFamily(), heightPixels)); // Set font size to match pixel height
        double brightness = 1.0 - (contrastPercent / 100.0) ;  //to calculate the brightness based on contrast given
        text.setFill(Color.gray(brightness)); // set the font's color, with contrast sensitivity in mind

        return text ;
    }

    /**
     * This method calculates the height in pixels of the letter we need to display
     * @return letter height in pixels
     */
    private double pixelHeightAfterCalibration(ArcMinutes arcMinutes) {
        double heightMM = arcMinutes.calculateHeightMeters() * 1000 ; //multiplier to convert from meters to mm
        double heightInches = heightMM / 25.4 ;  //convert from mm to inches because the pixel density is in inches
        double heightPixels = heightInches * screenPPI ; //scale for actual screen scale

        return heightPixels; //the rendering height for java fonts in logical pixels
    }

    /**
     * Method to calculate the size of the spaces between letters on the same row
     * @return size
     */
    public double letterSpacing() {
        Text sample = new Text("H") ;
        sample.setFont(Font.font(
                sloanFont.getFamily(),
                pixelHeightAfterCalibration(arcMinutes))) ;
        return sample.getBoundsInLocal().getWidth() ;
    }

    /**
     * Method to calculate the size of the spaces between rows
     * @return size
     */
    public double rowSpacing() {
        Text sample = new Text("H") ;
        sample.setFont(Font.font(
                sloanFont.getFamily(),
                pixelHeightAfterCalibration(arcMinutes))) ;
        return sample.getBoundsInLocal().getHeight() ;
    }
}