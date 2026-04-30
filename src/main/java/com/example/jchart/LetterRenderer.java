package com.example.jchart;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ChartRenderer {

    private static final String SLOAN_FONT_PATH = "/fonts/Sloan.otf" ;

    private ArcMinutes arcMinutes;
    private ScreenCalibration screenCalibration;
    private Font sloanFont ;

    public ChartRenderer(ArcMinutes arcMinutes, ScreenCalibration screenCalibration) {
        this.arcMinutes = arcMinutes;
        this.screenCalibration = screenCalibration;
        this.sloanFont = loadSloanFont() ;
    }

    private Font loadSloanFont() {
        Font font = Font.loadFont( //creates a font object, by loading the resource
                getClass().getResourceAsStream(SLOAN_FONT_PATH), 10 //size overridden later
        ) ;
        if (font == null) {
            System.err.println("Sloan font not found, falling back to default");
            return Font.font(10) ;
        }
        return font ;
    }

    /**
     * This method calculates the height in pixels of the letter we are displaying
     * @return letter height in pixels
     */
    public double heightAfterCalibration() {
        double height = arcMinutes.calculateHeight() ;
        double pixelsPerMM = screenCalibration.pixelsPerMillimeter() ;
        return height * pixelsPerMM ;
    }

    /**
     * Render a single letter (optotype) at the correct size, in Sloan font
     * @param letter: Character to display
     */
    public Text renderLetter(String letter) {
        double heightPixels = heightAfterCalibration() ; //size of the object in pixels
        Text text = new Text(letter); // Create JavaFX Text node
        text.setFill(Color.BLACK); // set the font's color
        text.setFont(Font.font(sloanFont.getFamily(), heightPixels)); // Set font size to match pixel height
        return text ;
    }
}