/**
 * Class that handles the rendering of individual letters
 * Loads the Sloan font from the resources folder
 * Calibrates height using the ArcMinutes
 * returns the Text with the correct font and point size
 */

package com.example.jchart;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class LetterRenderer {

    private ArcMinutes arcMinutes;

    //static variables to load in the font
    private static final String SLOAN_FONT_PATH = "/fonts/Sloan.otf" ;
    public static final Font sloanFont = loadSloanFont();

    private double outputScaleX ; //variable that determines the scale

    /**
     * Constructor for the letter renderer
     * @param arcMinutes
     * @param outputScaleX
     */
    public LetterRenderer(ArcMinutes arcMinutes, double outputScaleX) {
        this.arcMinutes = arcMinutes ;
        this.outputScaleX = outputScaleX ;
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
        double heightPoints = pointHeightAfterCalibration(arcMinutes) ; //size of the object in points
        Text text = new Text(letter); // Create JavaFX Text node
        text.setFont(Font.font(sloanFont.getFamily(), heightPoints)); // Set font size to match pixel height
        double brightness = 1.0 - (contrastPercent / 100.0) ;  //to calculate the brightness based on contrast given
        text.setFill(Color.gray(brightness)); // set the font's color, with contrast sensitivity in mind

        return text ;
    }

    /**
     * This method calculates the height in points of the letter we need to display
     * @return letter height in points
     */
    private double pointHeightAfterCalibration(ArcMinutes arcMinutes) {
        double heightMM = arcMinutes.calculateHeightMeters() * 1000 ; //multiplier to convert from meters to mm
        double heightPoints = heightMM * 72.0 / 25.4 ;

        return heightPoints / outputScaleX ; //the rendering height for java fonts divided by the screen's scale factor
    }

    /**
     * Method to calculate the size of the spaces between letters on the same row
     * @return size
     */
    public double letterSpacing() {
        Text sample = new Text("H") ;
        sample.setFont(Font.font(
                sloanFont.getFamily(),
                pointHeightAfterCalibration(arcMinutes))) ;
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
                pointHeightAfterCalibration(arcMinutes))) ;
        return sample.getBoundsInLocal().getHeight() ;
    }
}