/**
 * MainWindow is the class containing the user interface screen and the chart preview
 */

package io.github.janna99.jchart.ui;

import io.github.janna99.jchart.EyeChart;
import io.github.janna99.jchart.JChartController;
import io.github.janna99.jchart.LetterRenderer;
import io.github.janna99.jchart.settings.AppSettings;
import io.github.janna99.jchart.settings.SettingsManager;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;


public class MainWindow implements ChartStateListener{

    private final Stage stage; //the UI stage
    private Stage chartStage ; //the stage for the chart on the secondary screen
    private EyeChart chart ; //the one we have created an object for in the launchChart method

    //parameters
    private String chartType ;  //the type of chart we are using
    private double distanceMeters ;
    private double outputScaleX ; //the scale factor obtained from the screen itself

    //text and choice boxes
    private ChoiceBox<String> chartSelector ;
    private TextField distanceEntry ;

    //displays that need to be updated with the listener
    private VBox previewPane ;  //the pane that will populate with the eye chart data
    private Label contrastLevel ;  // empty until contrast changes

    /**
     * Constructor for the main window.
     * Sets up the preview pane, launches the chart, and updates the preview pane to show what is on the chart
     * @param stage
     * @param appSettings
     */
    public MainWindow(Stage stage, AppSettings appSettings) {
        this.stage = stage ;
        this.chartType = appSettings.getChartType() ;
        this.distanceMeters = appSettings.getDistanceMeters() ;

        // set up scene and stage with empty preview pane
        GridPane gridPane = buildGrid(appSettings) ;  //builds the UI menu with all of the inputs and buttons
        Scene scene = new Scene(gridPane,800, 600) ;
        stage.setScene(scene) ;
        stage.setTitle("JChart") ;
        stage.show() ;

        launchChart() ; //creates the eye chart itself, and the chartstage

        //to fill in the existing preview pane with the details from the eye chart just created in launchChart
        previewPane.getChildren().setAll(
                buildPreviewPane(chart.findIndexByDenominator(
                        JChartController.DEFAULT_BOTTOM_DENOMINATOR),
                        JChartController.DEFAULT_WINDOW_SIZE,
                        false).getChildren()) ;
        previewPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 5;") ;
    }

    /**
     * Method to display the chart in a secondary window, or the computer screen if secondary isn't available
     */
    public void launchChart() {
        if (chartStage != null) {
            chartStage.close() ;  //close the existing chartstage if there is already one
        }
        this.chartStage = new Stage() ; //creates a new stage for the chart itself

        if (Screen.getScreens().size() > 1) {
            Screen second = Screen.getScreens().get(1);
            Rectangle2D bounds = second.getVisualBounds() ;
            chartStage.setX(bounds.getMinX()) ;
            chartStage.setY(bounds.getMinY()) ;
            chartStage.setWidth(bounds.getWidth()) ;
            chartStage.setHeight(bounds.getHeight()) ;
        }

        chartStage.setScene(new Scene(new StackPane(), 800, 600)) ;
        chartStage.setFullScreen(true); //TODO this should be active for windows computers only, deactivate on macs

        chartStage.show() ;

        this.outputScaleX = chartStage.getOutputScaleX() ; //to obtain the screen's scale factor
        this.chart = new EyeChart(distanceMeters, chartType, outputScaleX) ;  //build out version 1 of the eye chart

        JChartController controller = new JChartController(chart, chartStage, stage) ;
        controller.setChartStateListener(this); //assign the chart state listener to this instance of MainWindow

        controller.refresh() ;
    }

    /**
     * Method to build the initial grid layout for the chart, before it has been fully launched with the button
     * @param settings - the application settings
     * @return the GridPane with all UI components, calls other methods
     */
    private GridPane buildGrid(AppSettings settings) {
        GridPane grid = new GridPane() ;
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);

        // column constraints
        ColumnConstraints leftColumn = new ColumnConstraints() ;
        leftColumn.setPercentWidth(30);
        ColumnConstraints middleColumn = new ColumnConstraints() ;
        middleColumn.setPercentWidth(40);
        ColumnConstraints rightColumn = new ColumnConstraints() ;
        rightColumn.setPercentWidth(30);
        grid.getColumnConstraints().addAll(leftColumn, middleColumn, rightColumn);

        // row constraints
        RowConstraints rowPreview = new RowConstraints();
        rowPreview.setPercentHeight(60);
        RowConstraints rowResolution = new RowConstraints();
        rowResolution.setPercentHeight(10);
        RowConstraints rowDistance = new RowConstraints();
        rowDistance.setPercentHeight(10);
        RowConstraints rowInstructions = new RowConstraints();
        rowInstructions.setPercentHeight(20);
        grid.getRowConstraints().addAll(rowPreview, rowResolution, rowDistance, rowInstructions);

        // add elements to the preview pane
        this.previewPane = new VBox(15) ;  //initialize blank previewPane because the chart must fill in later
        previewPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 5;") ;
        previewPane.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(previewPane, 3);
        GridPane.setHalignment(previewPane, HPos.CENTER);
        grid.add(previewPane, 0, 0);

        // contrast spans both columns, centered
        this.contrastLevel = new Label() ;
        GridPane.setColumnSpan(contrastLevel, 1);
        GridPane.setHalignment(contrastLevel, HPos.RIGHT);
        GridPane.setValignment(contrastLevel, VPos.BOTTOM);
        contrastLevel.setPadding(new Insets(15)) ;
        grid.add(contrastLevel, 2, 0);

        // chart type, distance and launch side by side
        HBox chartTypeSelector = chartTypeSelection(settings) ;  //initialize the choicebox for chart type
        HBox distanceInfo = buildDistanceInfo();  //initialize the distance entry
        Button update = buildUpdateButton(settings);  //initialize the launch chart button
        Button exit = buildExitButton() ;
        grid.add(chartTypeSelector, 0, 2) ;
        grid.add(distanceInfo, 1, 2);
        grid.add(update, 2, 2);
        grid.add(exit,2, 2) ;
        GridPane.setHalignment(chartTypeSelector, HPos.CENTER);
        GridPane.setHalignment(distanceInfo, HPos.CENTER);
        GridPane.setHalignment(update, HPos.LEFT);
        GridPane.setHalignment(exit, HPos.CENTER);

        // instructions in column index 3
        VBox instructions = buildInstructions();
        GridPane.setColumnSpan(instructions, 3);
        GridPane.setHalignment(instructions, HPos.CENTER);
        grid.add(instructions, 0, 3);

        return grid ;
    }

    /**
     * Method to create the preview of the letters on the eye chart
     * @param bottomIndex - lowest line
     * @param windowSize - quantity of lines displayed
     * @param isIsolated - single line setting
     * @return VBox containing the preview pane
     */
    public VBox buildPreviewPane(int bottomIndex, int windowSize, boolean isIsolated) {
        VBox chartPreview = new VBox(15) ;
        int topIndex = bottomIndex - windowSize + 1 ;

        if (isIsolated || topIndex > chart.getChartRows().size()) {
            chartPreview.getChildren().add(buildRowPreview(bottomIndex)) ;
        } else {
            for (int i = topIndex; i <= bottomIndex; i++) {
                chartPreview.getChildren().add(buildRowPreview(i)) ;
            }
        }
        chartPreview.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 5;") ;
        return chartPreview ;
    }

    /**
     * Switch method to call the methods to build the row preview for the assigned chart type
     * @param rowIndex - which line to display
     * @return HBox to display on the UI for the specific line preview
     */
    public HBox buildRowPreview(int rowIndex) {
        return switch (chartType) {
            case "Tumbling E" -> buildTumblingRowPreview(rowIndex, "E") ;
            case "Landolt C" -> buildTumblingRowPreview(rowIndex, "C") ;
            default -> buildSloanRowPreview(rowIndex) ;
        };
    }

    /**
     * Method to create a preview of the individual row for sloan charts
     * @param rowIndex - which line to display
     * @return HBox for the sloan row preview
     */
    public HBox buildSloanRowPreview(int rowIndex) {
        List<String> lettersInRow = chart.getLettersInRow(rowIndex) ;
        HBox row = new HBox(15) ;
        row.setAlignment(Pos.CENTER);

        for (String letter: lettersInRow) {
            Label letterLabel = new Label(letter) ;
            letterLabel.setFont(Font.font(LetterRenderer.sloanFont.getFamily(), previewFontSize(rowIndex))) ;
            row.getChildren().add(letterLabel) ;
        }
        Label acuityLabel = new Label("20/" + EyeChart.DENOMINATORS[rowIndex]) ;
        row.getChildren().add(acuityLabel) ;
        return row ;
    }

    /**
     * Method to create a preview of the individual row for tumbling charts
     * @param rowIndex - which line to display
     * @return HBox for the E/C row preview
     */
    public HBox buildTumblingRowPreview(int rowIndex, String eOrC) {
        List<String> eDegreesInRow = chart.getLettersInRow(rowIndex) ;
        HBox row = new HBox(15) ;
        row.setAlignment(Pos.CENTER);

        for (String degrees : eDegreesInRow) {
            Label eLabel = new Label(eOrC) ;
            eLabel.setFont(Font.font(LetterRenderer.sloanFont.getFamily(), previewFontSize(rowIndex))) ;
            eLabel.setRotate(Double.parseDouble(degrees)) ; //perform the rotation itself for each letter, setRotate is void
            row.getChildren().add(eLabel) ;  //add each randomly rotated letter to the tumbling E/C chart
        }
        Label acuityLabel = new Label("20/" + EyeChart.DENOMINATORS[rowIndex]) ;
        row.getChildren().add(acuityLabel) ;
        return row ;
    }

    /**
     * Method to calculate the font sizes for the preview display
     * @param rowIndex - the specific row to display
     * @return the size of the preview line
     */
    private double previewFontSize(int rowIndex) {
        double denominator = EyeChart.DENOMINATORS[rowIndex];
        double size = Math.max(6, denominator / 2.0); // simple ratio
        return size;
    }

    /**
     * Creates the UI element for the chart type selector
     * @param settings - app settings
     * @return HBox with dropdown menu to choose chart type
     */
    public HBox chartTypeSelection(AppSettings settings) {
        Label chartType = new Label("Chart Type: ") ;
        this.chartSelector = new ChoiceBox<>() ;
        chartSelector.getItems().addAll("Sloan", "Tumbling E", "Landolt C") ;
        chartSelector.getSelectionModel().select(settings.getChartType());
        chartSelector.setFocusTraversable(false);  //so that the arrow keys don't change it
        HBox chartTypeSelector = new HBox(15, chartType, chartSelector) ;
        chartTypeSelector.setAlignment(Pos.CENTER_LEFT);
        return chartTypeSelector ;
    }

    /**
     * Method to set the labels and fields for the distance entry
     * @return HBox for distance input
     */
    private HBox buildDistanceInfo() {
        Label distanceLabel = new Label("Distance:") ;
        this.distanceEntry = new TextField(String.valueOf(distanceMeters)) ;
        distanceEntry.setFocusTraversable(false) ;  //so that the arrow keys can't change it
        Label distanceUnits = new Label("m") ;
        HBox distanceInfo = new HBox(15, distanceLabel, distanceEntry, distanceUnits) ;
        distanceInfo.setAlignment(Pos.CENTER_LEFT);
        return distanceInfo;
    }

    /**
     * Builds the button that will update the chart when a different chart distance or type is selected
     * Also updates the AppSettings, and saves it with the SettingsManager static methods, and re-launches the chart
     * @param settings - app settings
     * @return update button
     */
    private Button buildUpdateButton(AppSettings settings) {
        Button update = new Button("Update") ; //button to save and launch the chart
        update.setFocusTraversable(false);  //so that the cursor doesn't highlight it
        update.setOnAction(event -> {
            try {
                this.distanceMeters = Double.parseDouble(distanceEntry.getText()) ;  //get the info from the UI
                this.chartType = chartSelector.getSelectionModel().getSelectedItem() ;
                settings.setDistanceMeters(distanceMeters) ;    //update the associated settings
                settings.setChartType(chartType) ;
                SettingsManager.save(settings) ;    //save the settings to the properties file for later use
                launchChart() ;  //launch the new chart
            } catch (NumberFormatException e) {
                distanceEntry.setStyle("-fx-border-color: red;"); //will highlight invalid entries
            }
        }) ;
        return update;
    }

    /**
     * Method to build the exit button for the app.
     * This gives a unified exit from the entire program rather than exiting out of the windows individually
     * @return exit button
     */
    private Button buildExitButton() {
        Button exit = new Button("Exit") ;
        exit.setFocusTraversable(false);
        exit.setOnAction(event -> System.exit(0)) ;
        return exit ;
    }

    /**
     * Builds the instructions information for the bottom panel of the UI
     * @return instructions box
     */
    private VBox buildInstructions() {
        Label title = new Label("Keyboard Controls:") ;
        Label upDown = new Label("↑↓:  Change size");
        Label space = new Label("SPACE:  Single line");
        Label leftRight = new Label("←→:  Change contrast");
        Label enter = new Label("ENTER:  Randomize chart");
        Label c = new Label("C:  Reset contrast");

        HBox instructions1 = new HBox(30);
        instructions1.setAlignment(Pos.CENTER);
        instructions1.getChildren().addAll(title);

        HBox instructions2 = new HBox(30);
        instructions2.setAlignment(Pos.CENTER);
        instructions2.getChildren().addAll(upDown, space, enter, leftRight, c);

        VBox instructions = new VBox(30) ;
        instructions.setAlignment(Pos.CENTER);
        instructions.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 5;");
        instructions.getChildren().addAll(instructions1, instructions2);
        return instructions ;
    }

    /**
     * Utilizes the interface ChartStateListener to update the preview pane and contrast display
     * when the parameters for the displayed chart have changed.
     * @param bottomIndex - lowest line
     * @param isIsolated - is it single line display
     * @param contrastPercent - letter displayed contrast
     */
    @Override
    public void onChartStateChanged(int bottomIndex, boolean isIsolated, int contrastPercent) {
        //to reset the contrast label on the MainWindow
        if (contrastPercent < 100) {
            contrastLevel.setText("Contrast: " + contrastPercent + "%") ;
            contrastLevel.setVisible(true) ;
        } else {
            contrastLevel.setVisible(false) ; //if the contrast is 100%, the contrast label will disappear
        }
        //to reset the preview pane
        previewPane.getChildren().clear(); //to remove the data
        previewPane.getChildren().addAll(buildPreviewPane(bottomIndex, JChartController.DEFAULT_WINDOW_SIZE, isIsolated).getChildren()) ;
    }

}

