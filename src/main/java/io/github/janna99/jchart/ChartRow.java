/**
 * Class handles the rendering of one row in the eye chart, one sloan (denominator) size per row
 * Creates Horizontal box with the text for use in JavaFx
 */

package io.github.janna99.jchart;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;

public class ChartRow {

    private List<String> letters ;  //the list of letters that will be displayed, or degrees for tumbling E/C
    private ArcMinutes arcMinutes ; //so that each row of the chart has its own acuity
    private final LetterRenderer letterRenderer ;  //create one instance of this

    private final double letterSpacing ;    // the spacing will be consistent
    private final double rowSpacing ;

    /**
     * Constructor for a single row of the chart
     * @param letters - the list of letters to be used on the chart line
     * @param denominator - will change throughout the testing process
     * @param distanceMeters - passed into the constructor, likely constant
     * @param outputScaleX - the screen scale factor obtained from the screen used for the chart
     */
    public ChartRow(List<String> letters,
                    double denominator,
                    double distanceMeters,
                    double outputScaleX) {
        this.letters = letters ;
        this.arcMinutes = new ArcMinutes(denominator, distanceMeters) ;
        this.letterRenderer = new LetterRenderer(arcMinutes, outputScaleX) ;
        this.letterSpacing =  letterRenderer.letterSpacing() ;
        this.rowSpacing = letterRenderer.rowSpacing() ;
    }

    /**
     * Switch method to select which kind of chart rows are rendering, and execute the associated methods
     * @param contrastPercent - default is 100% contrast,
     * @param chartType - default is standard sloan font
     * @return - HBox with specified optotype
     */
    public HBox renderRow(int contrastPercent, String chartType) {
        return switch (chartType) {
            case "Tumbling E" -> renderTumblingRow(contrastPercent, "E") ;
            case "Landolt C" -> renderTumblingRow(contrastPercent, "C") ;
            default -> renderSloanRow(contrastPercent) ;
        } ;
    }

    /**
     * Method for rendering the row of sloan letters
     * @param contrastPercent - default 100%
     * @return HBox with sloan letters
     */
    public HBox renderSloanRow(int contrastPercent) {
        HBox row = new HBox(letterSpacing); //clinically standardized spacing between letters
        row.setAlignment(Pos.CENTER);
        for (String letter : letters) {
            row.getChildren().add(letterRenderer.renderLetter(letter, contrastPercent)) ;
        }
        return row ;
    }

    /**
     * Method for rendering the row of Tumbling E or C letters
     * @param contrastPercent - default 100% contrast
     * @return row of either tumbling E or landolt C
     */
    public HBox renderTumblingRow(int contrastPercent, String eOrC) {
        HBox row = new HBox(letterSpacing) ;
        row.setAlignment(Pos.CENTER);
        for (String rotation : letters) {
            Text e = letterRenderer.renderLetter(eOrC, contrastPercent) ;    //create a text node to add to HBox row
            e.setRotate(Double.parseDouble(rotation)) ; //perform the rotation itself for each letter, setRotate is void
            row.getChildren().add(e) ;  //add each randomly rotated letter to the tumbling E or c chart
        }
        return row ;
    }

    /**
     * getter for the row spacing in the specific row
     * @return rowSpacing
     */
    public double getRowSpacing() {
        return rowSpacing ;
    }

    /**
     * getter method for the letters in the row
     * @return string of letters
     */
    public List<String> getLetters() {
        return letters ;
    }
}
