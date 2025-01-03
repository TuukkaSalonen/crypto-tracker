package fi.tuni.compse110.java3;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.CheckComboBox;

import fi.tuni.compse110.java3.preference.Preferences;
import fi.tuni.compse110.java3.preference.UserPreferenceManager;
import fi.tuni.compse110.java3.utility.ChartBounds;
import fi.tuni.compse110.java3.utility.ChartUtils;
import fi.tuni.compse110.java3.utility.Constants;
import fi.tuni.compse110.java3.utility.DialogUtils;
import fi.tuni.compse110.java3.utility.DragResizer;
import fi.tuni.compse110.java3.utility.NumberFormatter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Handles UI functionality.
 */
public class Controller {

    @FXML
    private CheckComboBox<String> cryptoCheckComboBox;

    @FXML
    private CheckComboBox<String> metalCheckComboBox;

    @FXML
    private CheckBox metalCheckBox;

    @FXML
    private CheckBox cryptoCheckBox;

    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private ComboBox<String> compareComboBox;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button updateButton;

    @FXML
    private Button searchButton;

    @FXML
    private MenuItem savePreferencesButton;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private TableView<Map<String, Object>> priceTable;

    @FXML
    private TableColumn<Map<String, Object>, LocalDate> dateColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> commodityColumn;

    @FXML
    private TableColumn<Map<String, Object>, Double> valueColumn;

    @FXML
    private Button resetTableButton;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label labelCurrency;

    @FXML
    private Label labelMetalUnit;

    @FXML
    private Label labelPieChartTitle;

    @FXML
    private Label labelPieChartDateRange;

    @FXML
    private Menu preferencesList;

    @FXML
    private MenuItem editPreferences;

    @FXML
    private MenuItem helpButton;

    @FXML
    private MenuItem aboutButton;

    @FXML
    private MenuItem resetButton;

    @FXML
    private Button resetChartButton;

    private ArrayList<TableColumn<Map<String, Object>, ?>> originalColumnOrder;

    // Variables for mouse dragging to zoom the chart
    private double mouseAnchorY, mouseAnchorX;

    // ChartBounds object to store the chart bounds
    private ChartBounds chartBounds;

    // Node to get the plot area
    private Node plotArea;

    private UserPreferenceManager preferenceManager;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Initializes the controller after the root element has been completely processed.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        preferenceManager = new UserPreferenceManager(this);

        DragResizer.makeResizable(priceTable);

        // Populate the CheckComboBox with cryptocurrencies
        cryptoCheckComboBox.getItems().addAll(API.getAvailableCryptocurrencies());

        // Populate the CheckComboBox with metals
        metalCheckComboBox.getItems().addAll(API.getAvailableMetals());

        // Populate the ComboBox with currency options
        currencyComboBox.getItems().addAll(API.getAvailableCurrencies());

        // Populate the ComboBox with comparison options
        compareComboBox.getItems().addAll("Price", "%-Change", "Volume");

        // Set default value for compareComboBox
        compareComboBox.setValue("Price");

        // Set default value for currencyComboBox
        currencyComboBox.setValue("USD");

        metalCheckComboBox.setDisable(true);

        priceTable.setEditable(false);

        labelMetalUnit.setVisible(false);
        labelPieChartTitle.setVisible(false);

        // Save the original column order to reset the table later
        originalColumnOrder = new ArrayList<>(priceTable.getColumns());

        resetButton.setOnAction(event -> resetToDefault());
        resetTableButton.setOnAction(event -> resetTable());
        resetChartButton.setOnAction(event -> resetChartZoom());

        resetChartButton.setDisable(true); // Not zoomed yet

        // Add listener to metalCheckBox
        metalCheckBox.setOnAction(event -> {
            if (metalCheckBox.isSelected()) {
                metalCheckComboBox.setDisable(false);
                // Disable the "Volume" option in the comparison ComboBox
                compareComboBox.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            if (item.equals("Volume")) {
                                setDisable(true);
                                setStyle("-fx-text-fill: grey;");
                            } else {
                                setDisable(false);
                                setStyle("");
                            }
                        } else {
                            setText(null);
                        }
                    }
                });
                if (compareComboBox.getValue().equals("Volume")) {
                    compareComboBox.setValue("Price");
                }
            } else {
                // Re-enable the "Volume" option when metalCheckBox is unchecked
                compareComboBox.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            setDisable(false);
                            setStyle("");
                        } else {
                            setText(null);
                        }
                    }
                });
                metalCheckComboBox.setDisable(true);
                metalCheckComboBox.getCheckModel().clearChecks();
            }
        });

        // Add listener to cryptoCheckBox
        cryptoCheckBox.setSelected(true);
        cryptoCheckBox.setOnAction(event -> {
            if (cryptoCheckBox.isSelected()) {
                cryptoCheckComboBox.setDisable(false);
            } else {
                // Clear the check model and disable the ComboBox when the CheckBox is unchecked
                cryptoCheckComboBox.setDisable(true);
                cryptoCheckComboBox.getCheckModel().clearChecks();
            }
        });

        // Add listener to the ComboBox to show/hide the currency ComboBox/label
        compareComboBox.setOnAction(event -> {
            String selected = compareComboBox.getValue();
            if (selected.equals("Volume")) {
                // Disable the metal checkbox and combobox if "Volume" is selected
                metalCheckBox.setDisable(true);
                metalCheckBox.setSelected(false);
                metalCheckComboBox.setDisable(true);
                metalCheckComboBox.getCheckModel().clearChecks();
            } else {
                metalCheckBox.setDisable(false);
            }
            if (selected.equals("Price")) {
                // Show the currency ComboBox and label
                currencyComboBox.setVisible(true);
                labelCurrency.setVisible(true);
            } else {
                // Hide the currency ComboBox and label
                currencyComboBox.setVisible(false);
                labelCurrency.setVisible(false);
            }
        });

        // Set default dates
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());

        LocalDate minDate = LocalDate.now().minusDays(364);

        // Configure the DatePickers to disable dates outside the allowed range
        configureDatePicker(startDatePicker, minDate, LocalDate.now());
        configureDatePicker(endDatePicker, minDate, LocalDate.now());

        // Validate selected start and end date after user input
        ChangeListener<LocalDate> dateChangeListener = (observable, oldValue, newValue) -> {
            if (startDatePicker.getValue() == null) {
                startDatePicker.setValue(LocalDate.now());
            }
            if (endDatePicker.getValue() == null) {
                endDatePicker.setValue(LocalDate.now());
            }

            final int maxDays = 365;

            // Limit date selection to 365 days
            if (endDatePicker.getValue().toEpochDay() - startDatePicker.getValue().toEpochDay() > maxDays ||
            // Also make sure start date is not after end date
                    startDatePicker.getValue().toEpochDay() > endDatePicker.getValue().toEpochDay()) {

                if (observable == startDatePicker.valueProperty()) {
                    endDatePicker.setValue(startDatePicker.getValue().plusDays(maxDays));
                } else if (observable == endDatePicker.valueProperty()) {
                    LocalDate yearAgo = LocalDate.now().minusDays(364);
                    startDatePicker.setValue(endDatePicker.getValue().minusDays(maxDays).isBefore(yearAgo) ? yearAgo
                            : endDatePicker.getValue().minusDays(maxDays));
                }
            }

            // Check if date is in the future
            long today = LocalDate.now().toEpochDay();
            if (endDatePicker.getValue().toEpochDay() > today) {
                if (startDatePicker.getValue().toEpochDay() > today) {
                    startDatePicker.setValue(LocalDate.now());
                }
                endDatePicker.setValue(LocalDate.now());
            }
        };

        // Add listener to the ComboBox to show/hide the metal ComboBox
        metalCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            metalCheckComboBox.setDisable(newValue);
            dateChangeListener.changed(startDatePicker.valueProperty(), startDatePicker.getValue(),
                    startDatePicker.getValue());
        });

        startDatePicker.valueProperty().addListener(dateChangeListener);
        endDatePicker.valueProperty().addListener(dateChangeListener);

        // Create a StringConverter for the DatePicker
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };

        // Set the converter to the DatePickers
        startDatePicker.setConverter(converter);
        endDatePicker.setConverter(converter);

        // Set the tick marks, minor ticks and labels invisible
        ChartUtils.setAxisTicksAndLabelsVisible(xAxis, false);
        ChartUtils.setAxisTicksAndLabelsVisible(yAxis, false);
        xAxis.setMinorTickVisible(false);
        yAxis.setMinorTickVisible(false);

        xAxis.setAutoRanging(false);

        // Set a tick label formatter for x-axis to show dates in a short format
        xAxis.setTickLabelFormatter(new StringConverter<>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.M.yy");

            @Override
            public String toString(Number object) {
                return Instant.ofEpochMilli(object.longValue())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(dateFormatter);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        // Set a tick label formatter for y-axis to format numbers using NumberFormatter
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                return NumberFormatter.formatNumber(object.doubleValue(), false);
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        // Set the chart plot area node
        plotArea = lineChart.lookup(".chart-plot-background");

        updateButton.setOnAction(event -> updateCharts());

        // Disable updateButton if neither checkbox is selected or no items are selected
        updateButton.disableProperty().bind(
                Bindings.createBooleanBinding(() -> (!cryptoCheckBox.isSelected()
                        || cryptoCheckComboBox.getCheckModel().getCheckedItems().isEmpty()) &&
                        (!metalCheckBox.isSelected() || metalCheckComboBox.getCheckModel().getCheckedItems().isEmpty()),
                        cryptoCheckBox.selectedProperty(),
                        metalCheckBox.selectedProperty(),
                        cryptoCheckComboBox.getCheckModel().getCheckedItems(),
                        metalCheckComboBox.getCheckModel().getCheckedItems()));

        savePreferencesButton.setOnAction(event -> preferenceManager.saveUserPreferences(
                List.copyOf(cryptoCheckComboBox.getCheckModel().getCheckedItems()),
                List.copyOf(metalCheckComboBox.getCheckModel().getCheckedItems()),
                startDatePicker.getValue(), endDatePicker.getValue(),
                currencyComboBox.getValue(), compareComboBox.getValue()));

        editPreferences.setOnAction(event -> preferenceManager.showEditPreferencesPopup());

        helpButton.setOnAction(event -> DialogUtils.showHelpDialog());
        aboutButton.setOnAction(event -> DialogUtils.showAboutDialog());

        searchButton.setOnAction(event -> openSearch());

        // Set custom cell value factories to access map entries correctly
        dateColumn.setCellValueFactory(cellData -> {
            Object dateValue = cellData.getValue().get("date");
            if (dateValue instanceof LocalDate) {
                return new SimpleObjectProperty<>((LocalDate) dateValue);
            } else {
                return new SimpleObjectProperty<>(null);
            }
        });
        // Set custom cell factory to format date correctly
        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });
        // Set custom cell value factory to access map entries correctly
        commodityColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty((String) cellData.getValue().get("commodity")));

        // Set custom cell factory to format double values using NumberFormatter
        valueColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(NumberFormatter.formatNumber(item, true));
                }
            }
        });
        // Set custom cell value factory to access map entries correctly
        valueColumn.setCellValueFactory(
                cellData -> new SimpleDoubleProperty((Double) cellData.getValue().get("value")).asObject());

        // Set sort types for the columns
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        commodityColumn.setSortType(TableColumn.SortType.ASCENDING);

        // Load user preferences on start from file
        preferenceManager.loadPreferences();
    }

    /**
     * Reset the UI to its default state.
     */
    private void resetToDefault() {
        // Clear CheckComboBoxes
        cryptoCheckComboBox.getCheckModel().clearChecks();
        cryptoCheckComboBox.setDisable(false);
        metalCheckComboBox.getCheckModel().clearChecks();

        // Reset CheckBoxes
        cryptoCheckBox.setSelected(true);
        metalCheckBox.setSelected(false);
        metalCheckComboBox.setDisable(true);

        compareComboBox.setValue("Price");
        currencyComboBox.setValue("USD");

        // Reset the ComboBox cell factory
        compareComboBox.setCellFactory(null);

        // Reset DatePickers
        startDatePicker.setValue(LocalDate.now().minusDays(30));
        endDatePicker.setValue(LocalDate.now());

        // Reset Labels
        labelMetalUnit.setVisible(false);
        labelPieChartTitle.setVisible(false);
        labelPieChartDateRange.setText("");
        yAxis.setLabel("");
        valueColumn.setText("Value");

        // Clear Charts and Table
        lineChart.getData().clear();
        priceTable.getItems().clear();
        pieChart.getData().clear();

        // Reset chart zoom and the table
        resetChartZoom();
        resetTable();

        // Set the tick marks, minor ticks and labels invisible
        ChartUtils.setAxisTicksAndLabelsVisible(xAxis, false);
        ChartUtils.setAxisTicksAndLabelsVisible(yAxis, false);

        // Reset chart zooming and dragging
        plotArea.setOnMousePressed(null);
        plotArea.setOnMouseDragged(null);
        lineChart.setOnScroll(null);

        // Reset cryptocurrencies to the default list
        API.resetCryptocurrencies();
        cryptoCheckComboBox.getItems().setAll(API.getAvailableCryptocurrencies());
    }

    /**
     * Open a search window to search for cryptocurrencies to add to the list.
     */
    private void openSearch() {
        ListView<String> searchResultsListView = new ListView<>();
        TextField searchTextField = new TextField();

        VBox vBox = new VBox(searchResultsListView, searchTextField);
        Scene scene = new Scene(vBox, 300, 200);
        Stage stage = new Stage();
        stage.setTitle("Crypto Search");
        stage.setScene(scene);
        DialogUtils.setStageIcon(stage);

        // Listen for searchTextField edit
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String search = newValue.trim().toLowerCase();

            if (search.isEmpty()) {
                searchResultsListView.getItems().setAll(Collections.emptyList());
                return;
            }

            // Get all available cryptocurrencies
            List<String> available = API.getAllAvailableCryptocurrencies();
            List<String> results = new ArrayList<>();

            // Filter the shown cryptocurrencies based on the search string
            for (String crypto : available) {
                if (crypto.toLowerCase().startsWith(search)) {
                    results.add(crypto);
                }
            }

            searchResultsListView.getItems().setAll(results);
        });

        // Listen for searchResultsListView click
        searchResultsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Add the selected cryptocurrency to the available list
                        API.addAvailableCryptocurrency(newValue);
                        List<String> selections = new ArrayList<>(
                                cryptoCheckComboBox.getCheckModel().getCheckedItems());
                        selections.add(newValue);
                        cryptoCheckComboBox.getItems().setAll(API.getAvailableCryptocurrencies());
                        for (String selected : selections) {
                            cryptoCheckComboBox.getItemBooleanProperty(selected).setValue(true);
                        }

                        cryptoCheckBox.setSelected(true);
                        cryptoCheckComboBox.setDisable(false);

                        stage.close();
                    }
                });

        // Allow closing search window by pressing ESC
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

        stage.show();
        searchTextField.requestFocus();
    }

    /**
     * Reset the table to its original state.
     */
    private void resetTable() {
        // Reset the column order to the original
        priceTable.getColumns().setAll(originalColumnOrder);

        // Reset the table height
        priceTable.setMinHeight(Constants.TABLE_MIN_Y_LIMIT);

        // Revert column widths back to the original values
        dateColumn.setMinWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);
        dateColumn.setPrefWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);
        dateColumn.setMaxWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);

        commodityColumn.setMinWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);
        commodityColumn.setPrefWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);
        commodityColumn.setMaxWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);

        valueColumn.setMinWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);
        valueColumn.setPrefWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);
        valueColumn.setMaxWidth(Constants.DEFAULT_TABLE_COLUMN_WIDTH);

        // Reset sorting
        priceTable.getSortOrder().clear();

        // Set sort type for dateColumn to ensure newest dates are first
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        commodityColumn.setSortType(TableColumn.SortType.ASCENDING);

        // Sort by date, then by currency name and finally by price
        priceTable.getSortOrder().addAll(originalColumnOrder);

        // Set min/max column sizes to allow resizing again
        dateColumn.setMaxWidth(Double.MAX_VALUE);
        commodityColumn.setMaxWidth(Double.MAX_VALUE);
        valueColumn.setMaxWidth(Double.MAX_VALUE);

        dateColumn.setMinWidth(Constants.MIN_TABLE_COLUMN_WIDTH);
        commodityColumn.setMinWidth(Constants.MIN_TABLE_COLUMN_WIDTH);
        valueColumn.setMinWidth(Constants.MIN_TABLE_COLUMN_WIDTH);

        // Refresh the table to apply the changes
        priceTable.refresh();
    }

    /**
     * Configure the DatePicker to disable dates outside the allowed range.
     * Update the style of disabled dates.
     * 
     * @param datePicker The DatePicker to configure
     * @param minDate    The minimum allowed date
     * @param maxDate    The maximum allowed date
     */
    private void configureDatePicker(DatePicker datePicker, LocalDate minDate, LocalDate maxDate) {
        // Set a DayCellFactory to disable dates outside the allowed range
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(minDate) || date.isAfter(maxDate)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #d3d3d3; -fx-text-fill: #a9a9a9;");
                }
            }
        });
    }

    /**
     * Update the charts with the selected comparison type, cryptocurrencies, metals, and date range.
     */
    private void updateCharts() {
        lineChart.getData().clear();
        priceTable.getItems().clear();
        pieChart.getData().clear();

        // Update the x-axis date range based on the selected start and end dates
        ChartUtils.updateAxisDateRange(xAxis, yAxis, startDatePicker.getValue(), endDatePicker.getValue());

        String comparisonType = compareComboBox.getValue();
        String exchangeCurrency = currencyComboBox.getValue();
        String valueColumnHeader = "";

        // Set the value column header and tooltip label based on the comparison type
        String tooltipLabel = switch (comparisonType) {
            case "Price" -> {
                valueColumnHeader = "Price (" + exchangeCurrency + ")";
                yield "Price (" + exchangeCurrency + ")";
            }
            case "%-Change" -> {
                valueColumnHeader = "%-Change";
                yield "%-Change";
            }
            case "Volume" -> {
                valueColumnHeader = "Volume";
                yield "Volume";
            }
            default -> "";
        };

        valueColumn.setText(valueColumnHeader);
        // Set the y-axis label based on the comparison type and exchange currency
        ChartUtils.setAxisLabel(yAxis, comparisonType, exchangeCurrency);

        // Get selected currencies, metals, and date range
        List<String> selectedCurrencies = cryptoCheckComboBox.getCheckModel().getCheckedItems();
        List<String> selectedMetals = metalCheckComboBox.getCheckModel().getCheckedItems();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Set visibility of labelMetalUnit based on whether metals are included
        labelMetalUnit.setVisible(!selectedMetals.isEmpty() && comparisonType.equals("Price"));

        if (selectedCurrencies.isEmpty() && selectedMetals.isEmpty()) {
            return; // Nothing selected
        }

        Map<String, List<XYChart.Data<Number, Number>>> cryptoPrices = API.getCryptos(selectedCurrencies, startDate,
                endDate, exchangeCurrency, comparisonType.equals("Volume"));

        Map<String, List<XYChart.Data<Number, Number>>> metalPrices = API.getMetals(selectedMetals, startDate, endDate,
                exchangeCurrency);

        // In case data was not found for some crypto
        selectedCurrencies = new ArrayList<>(cryptoPrices.keySet());
        selectedMetals = new ArrayList<>(metalPrices.keySet());

        if (selectedCurrencies.isEmpty() && selectedMetals.isEmpty()) {
            return; // Nothing selected
        }

        Map<String, List<XYChart.Data<Number, Number>>> cryptoVolumes = API.getCryptos(selectedCurrencies, startDate,
                endDate, exchangeCurrency, true);

        // If %-change is selected, calculate the percentage change and keep the original data for pie chart
        Map<String, List<XYChart.Data<Number, Number>>> cryptoPricesMod = comparisonType.equals("%-Change")
                ? ChartUtils.calculatePercentChange(cryptoPrices)
                : cryptoPrices;

        // If %-change is selected, calculate the percentage change for each metal
        metalPrices = comparisonType.equals("%-Change") ? ChartUtils.calculatePercentChange(metalPrices) : metalPrices;

        // Populate line chart and table with data for currencies
        ChartUtils.populateChart(cryptoPricesMod, dateFormatter, selectedCurrencies, tooltipLabel, priceTable,
                lineChart);

        // Populate line chart and table with data for metals
        ChartUtils.populateChart(metalPrices, dateFormatter, selectedMetals, tooltipLabel, priceTable,
                lineChart);

        // Populate pie chart with volume data for cryptocurrencies
        ChartUtils.populatePieChart(cryptoVolumes, selectedCurrencies, pieChart);

        chartBounds = new ChartBounds(lineChart, plotArea);

        // Set chart zooming functionality
        lineChart.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            if (deltaY == 0)
                return; // Ignore if there's no scroll

            // Check if the scroll event occurred within the bounds of the plotArea
            Bounds plotBounds = plotArea.localToScene(plotArea.getBoundsInLocal());
            if (!plotBounds.contains(event.getSceneX(), event.getSceneY())) {
                return; // Ignore if the scroll event is outside the plotArea
            }
            // If it is, consume the event to prevent it from propagating to the scene
            event.consume();
            double zoomFactor = (deltaY > 0) ? Constants.ZOOM_FACTOR_IN : Constants.ZOOM_FACTOR_OUT;

            // Calculate the coordinates relative to the plotArea
            double x = event.getX() - plotArea.getLayoutX();
            double y = event.getY() - plotArea.getLayoutY();

            zoomChart(zoomFactor, x, y, chartBounds);
        });

        // Set chart dragging functionality
        plotArea.setOnMousePressed(this::handleChartPressed);
        plotArea.setOnMouseDragged(this::handleChartDragged);

        resetChartZoom();

        // Set visibility of pie chart title and date range label based on if any cryptos are selected
        labelPieChartTitle.setVisible(!selectedCurrencies.isEmpty());
        String dateRangeText = String.format("Total volumes: %s - %s", startDate.format(dateFormatter),
                endDate.format(dateFormatter));
        labelPieChartDateRange.setText(selectedCurrencies.isEmpty() ? "" : dateRangeText);

        // Apply sorting after the table has been populated
        priceTable.getSortOrder().clear();
        priceTable.getSortOrder().addAll(originalColumnOrder);
        priceTable.refresh();
    }

    /**
     * Add loaded user preferences to preferences list.
     *
     * @param loadedPreferencesList the list of loaded user preferences
     */
    public void displayUserPreferences(List<Preferences> loadedPreferencesList) {
        preferencesList.getItems().clear();
        for (Preferences preference : loadedPreferencesList) {
            MenuItem menuItem = new MenuItem(preference.getName());
            menuItem.setOnAction(event -> loadUserPreference(preference));
            preferencesList.getItems().add(menuItem);
        }
    }

    /**
     * Load user preferences and update the UI.
     *
     * @param preferences the Preferences object containing the user preferences
     */
    private void loadUserPreference(Preferences preferences) {
        if (preferences == null) {
            DialogUtils.showWarningDialog("Failed", "No saved preferences found");
            return;
        }

        cryptoCheckComboBox.getCheckModel().clearChecks();
        metalCheckComboBox.getCheckModel().clearChecks();

        for (String cryptoCurrency : preferences.getSelectedCryptocurrencies()) {
            cryptoCheckComboBox.getCheckModel().check(cryptoCurrency);
        }

        // Disable the ComboBox if no currencies are selected and uncheck the CheckBox
        if (preferences.getSelectedCryptocurrencies() == null
                || preferences.getSelectedCryptocurrencies().isEmpty()) {
            cryptoCheckBox.setSelected(false);
            cryptoCheckComboBox.setDisable(true);
        } else {
            // Enable the ComboBox if currencies are selected and check the CheckBox
            cryptoCheckBox.setSelected(true);
            cryptoCheckComboBox.setDisable(false);
        }

        for (String metal : preferences.getSelectedMetals()) {
            metalCheckComboBox.getCheckModel().check(metal);
        }

        // Disable the ComboBox if no metals are selected and uncheck the CheckBox
        if (preferences.getSelectedMetals() == null || preferences.getSelectedMetals().isEmpty()) {
            metalCheckBox.setSelected(false);
            metalCheckComboBox.setDisable(true);
        } else {
            // Enable the ComboBox if metals are selected and check the CheckBox
            metalCheckBox.setSelected(true);
            metalCheckComboBox.setDisable(false);
        }

        compareComboBox.setValue(preferences.getSelectedComparisonType());
        startDatePicker.setValue(preferences.getStartDate());
        endDatePicker.setValue(preferences.getEndDate());
        currencyComboBox.setValue(preferences.getSelectedCurrency());

        updateCharts();
    }

    /**
     * Zooms the chart based on the zoom factor and mouse position.
     *
     * @param zoomFactor  the factor by which to zoom the chart
     * @param mouseX      the x-coordinate of the mouse position
     * @param mouseY      the y-coordinate of the mouse position
     * @param chartbounds the ChartBounds object containing the maximum and minimum
     *                    values of the chart
     */
    private void zoomChart(double zoomFactor, double mouseX, double mouseY, ChartBounds chartbounds) {
        // Zoom the x and y axes based on the mouse position
        chartbounds.zoomXAxis(mouseX, zoomFactor);
        chartbounds.zoomYAxis(mouseY, zoomFactor);
        // Check if the zoomout has reached the maximum values
        chartbounds.checkZoomReset();
        resetChartButton.setDisable(false);
    }

    /**
     * Handles the mouse pressed event to initialize the chart's NumberAxis for dragging.
     *
     * @param event the MouseEvent triggered by pressing the mouse
     */
    private void handleChartPressed(MouseEvent event) {
        mouseAnchorY = event.getY();
        mouseAnchorX = event.getX();
        // Set chartbounds to current state
        chartBounds.setChartPressed();
    }

    /**
     * Handles the mouse dragged event to update the chart's NumberAxis.
     *
     * @param event the MouseEvent triggered by dragging the mouse
     */
    private void handleChartDragged(MouseEvent event) {
        // Check if all nodes are visible, if so, return
        if (chartBounds.allNodesVisible()) {
            return;
        }
        double deltaY = event.getY() - mouseAnchorY;
        double deltaX = event.getX() - mouseAnchorX;

        // Update the chart bounds based on the mouse drag
        chartBounds.dragYAxis(deltaY);
        chartBounds.dragXAxis(deltaX);
    }

    /**
     * Reset the chart zoom to the original state.
     */
    private void resetChartZoom() {
        // Reset the chart zoom to the original state if chartbounds exist
        if (chartBounds != null) {
            chartBounds.resetChartZoom();
        }
        resetChartButton.setDisable(true);
    }
}
