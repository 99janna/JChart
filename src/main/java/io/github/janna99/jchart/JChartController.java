/**
 * Controller class for the EyeChart
 */

package io.github.janna99.jchart;

import io.github.janna99.jchart.ui.ChartStateListener;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent ;

public class JChartController {

    private final EyeChart eyeChart ;   //instance of the eyeChart
    private final StackPane root = new StackPane() ;
    private final Stage chartStage ;
    private final Stage mainStage ;

    public final static int DEFAULT_WINDOW_SIZE = 3 ;
    public final static int DEFAULT_BOTTOM_DENOMINATOR = 20 ;
    private int currentWindowSize;     //the default will be 3 lines at a time
    private int currentBottomIndex ;   //the index of the bottom line or individual line, same for either

    private static final int MAX_CONTRAST = 100 ;
    private static final int MIN_CONTRAST = 1 ;
    private static final int CONTRAST_STEP = 1 ;
    private int currentContrastPercent ;  //default full contrast

    private boolean isIsolated ;    //when true this means that there is one single line, or else there is the default 3

    private ChartStateListener chartStateListener ;  //field for the listener as the chart updates

    /**
     * Constructor for the chart controller class. Assigns the chartStage to the root (StackPane), calls the
     * keyboard assignment method, and refreshes the eye chart for display
     * @param eyeChart - the chart object
     * @param chartStage - the Stage for the chart to be displayed on
     * @param mainStage - the Stage for the user interface and chart preview pane
     */
    public JChartController(EyeChart eyeChart, Stage chartStage, Stage mainStage) {
        this.eyeChart = eyeChart ;
        this.chartStage = chartStage ;
        this.mainStage = mainStage ;
        this.currentBottomIndex = eyeChart.findIndexByDenominator(DEFAULT_BOTTOM_DENOMINATOR) ; //to initialize the 20/20 line
        this.currentWindowSize = DEFAULT_WINDOW_SIZE ;  //initialize 3 lines as the default, can be changed to 1
        this.currentContrastPercent = MAX_CONTRAST ;  //to default the contrast to 100%
        this.isIsolated = false ;  //to default to multi line rather than single

        chartStage.getScene().setRoot(root) ;
        root.setAlignment(Pos.CENTER);      //to set up the desired position for the stackpane itself
        root.setStyle("-fx-background-color: white;");

        setupKeyboardControls() ;
        refresh() ;
    }

    /**
     * Binds all keyboard controls to the primary stage
     */
    private void setupKeyboardControls() {
        //set up chartStage controls
        chartStage.getScene().setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        //set up the mainStage (UI) controls. Must override natural controls for the gridpane, but only
        //for the ones we will use to control our chart itself
        mainStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()) {
                case UP, DOWN, SPACE, ENTER, LEFT, RIGHT, C -> {
                    handleKeyPress(event.getCode());
                    event.consume(); //prevent default textbox and button behaviour
                }
            }
        });
    }

    /**
     * Switch method to assign keyboard keys to their corresponding method
     * @param key - the keyboard key to assign to each method
     */
    private void handleKeyPress(KeyCode key) {
        switch (key) {
            case UP -> shiftWindow(-1) ;
            case DOWN -> shiftWindow(+1) ;
            case SPACE -> toggleIsolate() ;
            case ENTER -> randomizeChart() ;
            case RIGHT -> changeContrast(+CONTRAST_STEP) ;
            case LEFT -> changeContrast(-CONTRAST_STEP) ;
            case C -> resetContrast() ;
        }
    }

    /**
     * Method to refresh the eye chart and update the chart state listener
     */
    public void refresh() {
        VBox chartBox ;
        if (isIsolated) {
            currentWindowSize = 1 ;
        } else {
            currentWindowSize = DEFAULT_WINDOW_SIZE;
        }
        chartBox = eyeChart.renderChartRows(currentBottomIndex, currentWindowSize, currentContrastPercent) ;
        root.getChildren().setAll(chartBox) ;

        if (chartStateListener != null) {
            chartStateListener.onChartStateChanged(currentBottomIndex, isIsolated, currentContrastPercent) ;
        }
    }

    /**
     * Method to change which lines are visible
     * @param delta - increment of change for the display
     */
    private void shiftWindow(int delta) {
        int newBottomIndex = currentBottomIndex + delta ;
        int newTopIndex = newBottomIndex - currentWindowSize + 1 ;

        if (newBottomIndex >= eyeChart.getChartRows().size() || newTopIndex < 0) {
            return ;
        }
        currentBottomIndex = newBottomIndex ;
        refresh() ;
    }

    /**
     * Method to randomize the letters on the EyeChart
     */
    private void randomizeChart() {
        eyeChart.randomizeChart() ;
        refresh() ;
    }

    /**
     * Call this method to switch between single line and multi line display
     */
    private void toggleIsolate() {
        if (!isIsolated) {
            isIsolated = true ;  //can always change this, always safe
            refresh() ;
        } else {  //the case where the line is already isolated, check if we can widen window
            int targetTopIndex = currentBottomIndex - DEFAULT_WINDOW_SIZE + 1 ;
            if (targetTopIndex < 0) {
                return ;
            }
            isIsolated = false ;
            refresh() ;
        }
    }

    /**
     * Method to change the contrast level by a specified interval
     * @param delta - amount of change in contrast per increment
     */
    private void changeContrast(int delta) {
        if ((currentContrastPercent + delta) > 10) {
            delta = delta * 10 ;  //to make the contrast changes smaller at smaller sloan sizes
        }
        if ((currentContrastPercent + delta) < MIN_CONTRAST || (currentContrastPercent + delta) > MAX_CONTRAST) {
            return ; // if the contrast amount would be out of range, return
        }
        currentContrastPercent += delta ;
        refresh() ;
    }

    /**
     * Method to reset the contrast back to the maximum
     */
    private void resetContrast() {
        currentContrastPercent = MAX_CONTRAST ;
        refresh() ;
    }

    /**
     * Setter method to assign the chart state listener to this instance
     * @param listener - to check for changes in the chart being displayed
     */
    public void setChartStateListener(ChartStateListener listener) {
        this.chartStateListener = listener ;
    }

}
