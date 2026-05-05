/**
 * Class to generate the EyeChart, factoring in the distance, type of chart, and scale.
 */

package io.github.janna99.jchart;

import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;

public class EyeChart {

    private final Random random = new Random() ;  //create random object to randomize the letters
    private int rowLength = 5 ;     //standard length of the rows is 5, but this may be modified

    private List<ChartRow> chartRows ;  //a list of chartrow objects to make up the eye chart
    private double distanceMeters ; //the passed variable for the chart distance in meters
    private String chartType ; //which type of chart
    private double outputScaleX ; //the screen's scale factor

    //Arrays of constants used in the eye chart, including letter options and sloan denominators
    private static final String[] SLOAN_LETTERS = {"C", "D", "H", "K", "N", "O", "R", "S", "V", "Z"} ;
    private static final String[] TUMBLING_ROTATIONS = {"0", "90", "180", "270"}; //for use in rotation of E/C
    public static final int[] DENOMINATORS = {200, 150, 100, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10} ;

    /**
     * Constructor for the eye chart. Will consist of multiple rows of ChartRow objects
     * @param distanceMeters - chart distance
     * @param chartType - chart type
     * @param outputScaleX - scale factor for the screen
     */
    public EyeChart(double distanceMeters, String chartType, double outputScaleX) {
        this.distanceMeters = distanceMeters ;
        this.chartType = chartType ;
        this.outputScaleX = outputScaleX ;
        this.chartRows = buildChart() ; //method to build the List of ChartRow objects
    }

    /**
     * Method to return the index on the denominator list, for viewing the chart in single or multiple lines
     * @param denominator - Snellen/sloan denominator for the chart
     * @return the index of the denominator from DENOMINATORS array
     */
    public int findIndexByDenominator(int denominator) {
        for (int i = 0; i < DENOMINATORS.length; i++) {
            if (DENOMINATORS[i] == denominator) {
                return i ;
            }
        }
        System.err.println("Denominator " + denominator + " not found, defaulting to 0") ;
        return 0 ;
    }

    /**
     * Switch method that selects and builds the full chart of the selected type
     * @return the List of ChartRow objects for the EyeChart
     */
    private List<ChartRow> buildChart() {
        return switch (chartType) {
            case "Tumbling E", "Landolt C" -> buildTumblingRows() ;
            default -> buildSloanRows() ;
        } ;
    }

    /**
     * Method to randomize the chart's optotypes
     */
    public void randomizeChart() {
        this.chartRows = buildChart() ;
    }

    /**
     * Method to render the chart rows specified by line index of the DENOMINATORS array
     * @param bottomIndex - DENOMINATORS array index of the bottom line being displayed
     * @param countLines - quantity of lines being displayed on the chart at the time
     * @param contrastPercent - the contrast of the letters being displayed on the chart
     * @return Vbox object the displays all the HBox objects
     */
    public VBox renderChartRows(int bottomIndex, int countLines, int contrastPercent) {
        int topIndex = bottomIndex - countLines + 1 ;  //calculate the top index

        if (countLines <= 0 || bottomIndex >= chartRows.size() || topIndex < 0) {
            System.err.println("Invalid row indices " + topIndex + " to " + bottomIndex);
            return new VBox() ;
        }

        VBox chart = new VBox() ;
        chart.setAlignment(Pos.CENTER) ;

        for (int i = topIndex; i <= bottomIndex; i++) {
            chart.getChildren().add(chartRows.get(i).renderRow(contrastPercent, chartType)) ;

            if (i < bottomIndex) {  //add this spacing for every row but the last
                Pane spacer = new Pane() ;
                spacer.setPrefHeight(chartRows.get(i).getRowSpacing()) ;
                chart.getChildren().add(spacer) ;
            }
        }
        return chart ;
    }

    /**
     * We want to build all the rows that will be on the eye chart
     * This should be dynamic and randomized, new letters each time it is displayed
     * @return sloan chart
     */
    private List<ChartRow> buildSloanRows() {
        List<ChartRow> chart = new ArrayList<>() ;  //initialize the List of ChartRow objects for the chart
        for (int denominator : DENOMINATORS) {
            List<String> rowOfLetters = randomizeSloanLine() ;
            chart.add(new ChartRow(rowOfLetters, denominator, distanceMeters, outputScaleX)) ;
        }
        return chart ;
    }

    /**
     * Generate a randomized row to be used in the buildSloanRows() method
     * @return the list of letters for the sloan line
     */
    private List<String> randomizeSloanLine() {
        List<String> randomizedSloan = new ArrayList<>(Arrays.asList(SLOAN_LETTERS)) ;
        Collections.shuffle(randomizedSloan, random);
        return randomizedSloan.subList(0, rowLength) ;
    }

    /**
     * Method to build the rows that will be on the tumbling E/C rows, the "letters" are String
     * versions of the degrees of rotation, to be rendered later
     * @return tumbling E/C chart
     */
    private List<ChartRow> buildTumblingRows() {
        List<ChartRow> chart = new ArrayList<>() ; //initialize the list of chartRow objects for this chart
        for (int denominator : DENOMINATORS) {
            List<String> rowOfDegrees = randomizeTumblingLine() ;
            chart.add(new ChartRow(rowOfDegrees, denominator, distanceMeters, outputScaleX)) ;
        }
        return chart ;
    }

    /**
     * Method to randomize the four orientations possible for the tumbling E/C chart
     * @return randomizedDegrees
     */
    private List<String> randomizeTumblingLine() {
        List<String> randomizedDegrees = new ArrayList<>() ;
        for (int i = 0; i < rowLength; i++) {
            int randomIndex = random.nextInt(TUMBLING_ROTATIONS.length) ;
            randomizedDegrees.add(TUMBLING_ROTATIONS[randomIndex]) ;
        }
        return randomizedDegrees ;
    }

    /**
     * getter method for the EyeChart's ChartRow objects
     * @return chartRows
     */
    public List<ChartRow> getChartRows() {
        return chartRows;
    }

    /**
     * getter method for the letters in a specific row
     * @param rowIndex - index for the specific ChartRow object in the chartRows list
     * @return the letters
     */
    public List<String> getLettersInRow(int rowIndex) {
        return chartRows.get(rowIndex).getLetters();
    }

}
