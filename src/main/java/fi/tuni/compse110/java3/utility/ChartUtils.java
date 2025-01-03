package fi.tuni.compse110.java3.utility;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Class for utility methods related to charts.
 */
public class ChartUtils {

    private static final double[] sensibleValues = { 1, 2, 5, 10 };

    /**
     * Round the tick value to a sensible value.
     * 
     * @param value the value to round
     * @return the rounded value
     */
    public static double roundTickValue(double value) {
        double base = Math.pow(10, Math.floor(Math.log10(value)));
        for (double sensibleValue : sensibleValues) {
            double roundedValue = base * sensibleValue;
            if (value <= roundedValue) {
                return roundedValue;
            }
        }
        return base * 10;
    }

    /**
     * Round the value based on its magnitude.
     * 
     * @param value the value to round
     * @return the rounded value
     */
    public static double roundChartValue(double value) {
        if (value >= 10) {
            return Math.round(value);
        } else {
            int decimalPlaces = 0;
            double tempValue = value;
            while (tempValue < 1 && decimalPlaces < 10) { // Limit to 10 decimal places to avoid infinite loop
                tempValue *= 10;
                decimalPlaces++;
            }
            decimalPlaces++;
            double scale = Math.pow(10, decimalPlaces);
            return Math.round(value * scale) / scale;
        }
    }

    /**
     * Floor the value based on its magnitude.
     * 
     * @param value the value to floor
     * @return the floored value
     */
    public static double floorChartValue(double value) {
        if (value <= -1) {
            return Math.floor(value);
        } else {
            int decimalPlaces = 0;
            double tempValue = value;
            while (tempValue > -1 && decimalPlaces < 10) { // Limit to 10 decimal places to avoid infinite loop
                tempValue *= 10;
                decimalPlaces++;
            }
            double scale = Math.pow(10, decimalPlaces);
            return Math.floor(value * scale) / scale;
        }
    }

    /**
     * Calculate the tick unit based on the range for y-axis.
     * 
     * @param range the range of the y-axis
     * @return the tick unit
     */
    public static double calculateYTickUnit(double range) {
        int minTicks = 5;
        double minTickUnit = 0.1;

        double base = Math.pow(10, Math.floor(Math.log10(range / minTicks)));
        double multiplier;

        if (range < 1) {
            multiplier = (range / base < 2) ? 0.1 : (range / base < 5) ? 0.2 : 0.5;
        } else {
            multiplier = (range / base < 2) ? 1 : (range / base < 5) ? 2 : 5;
        }

        double tickUnit = base * multiplier;

        // Adjust the tick unit if it results in too few ticks
        if (range / tickUnit < minTicks) {
            tickUnit = range / minTicks;
        }

        // Ensure the tick unit is appropriately small for smaller ranges
        if (range < 1) {
            tickUnit = Math.min(tickUnit, 0.1);
        } else if (range < 10) {
            tickUnit = Math.min(tickUnit, 1);
        } else if (range < 100) {
            tickUnit = Math.min(tickUnit, 10);
        } else if (range < 1000) {
            tickUnit = Math.min(tickUnit, 100);
        } else if (range < 5000) {
            tickUnit = Math.min(tickUnit, 250);
        } else if (range < 10000) {
            tickUnit = Math.min(tickUnit, 500);
        } else if (range < 20000) {
            tickUnit = Math.min(tickUnit, 1000);
        }

        return roundTickValue(Math.max(tickUnit, minTickUnit));
    }

    /**
     * Calculate the tick unit based on the total days for x-axis.
     * 
     * @param totalDays the total days
     * @return the tick unit
     */
    public static double getTickUnit(int totalDays) {
        if (totalDays <= 7) {
            return Constants.DAY;
        } else if (totalDays <= 14) {
            return Constants.DAY * 2;
        } else if (totalDays <= 30) {
            return Constants.DAY * 3;
        } else if (totalDays <= 90) {
            return Constants.DAY * 7;
        } else if (totalDays <= 180) {
            return Constants.DAY * 14;
        } else {
            return Constants.DAY * 30;
        }
    }

    /**
     * Calculate the maximum value in the line chart y-axis.
     * 
     * @param chart the line chart
     * @return the maximum value
     */
    public static double calculateMaxValueY(LineChart<Number, Number> chart) {
        return chart.getData().stream()
                .flatMap(series -> series.getData().stream())
                .mapToDouble(data -> data.getYValue().doubleValue())
                .max()
                .orElse(0.0);
    }

    /**
     * Calculate the minimum value in the line chart y-axis.
     * 
     * @param chart the line chart
     * @return the minimum value
     */
    public static double calculateMinValueY(LineChart<Number, Number> chart) {
        return chart.getData().stream()
                .flatMap(series -> series.getData().stream())
                .mapToDouble(data -> data.getYValue().doubleValue())
                .min()
                .orElse(0.0);
    }

    /**
     * Calculate the percentage change for each commodity compared to the first
     * value (earliest) in the list.
     * 
     * @param dataMap map containing the commodity data
     * @return map containing the percentage change data
     */
    public static Map<String, List<XYChart.Data<Number, Number>>> calculatePercentChange(
            Map<String, List<XYChart.Data<Number, Number>>> dataMap) {
        Map<String, List<XYChart.Data<Number, Number>>> percentChangeMap = new HashMap<>();

        // Calculate the percentage change for each commodity
        for (String crypto : dataMap.keySet()) {
            List<XYChart.Data<Number, Number>> dataList = dataMap.get(crypto);
            List<XYChart.Data<Number, Number>> dailyChanges = new ArrayList<>();

            if (!dataList.isEmpty()) {
                // If current price is added to the start (happens with cryptos), put to the end
                if (dataList.get(0).getXValue().longValue() > dataList.get(1).getXValue().longValue()) {
                    XYChart.Data<Number, Number> firstElement = dataList.remove(0);
                    dataList.add(firstElement);
                }
                // Use the first value as the base with 0% change
                double basePrice = dataList.get(0).getYValue().doubleValue();
                Number baseXValue = dataList.get(0).getXValue();
                dailyChanges.add(new XYChart.Data<>(baseXValue, 0.0));

                // Calculate the percentage change for each commodity node
                for (int i = 1; i < dataList.size(); i++) {
                    double currentPrice = dataList.get(i).getYValue().doubleValue();
                    double percentageChange = ((currentPrice - basePrice) / basePrice) * 100;
                    Number xValue = dataList.get(i).getXValue();
                    dailyChanges.add(new XYChart.Data<>(xValue, percentageChange));
                }
            }
            percentChangeMap.put(crypto, dailyChanges);
        }
        return percentChangeMap;
    }

    /**
     * Customize a data node in the line chart to scale on hover.
     *
     * @param data       The data node to customize
     * @param baseScale  The initial scale of the node
     * @param hoverScale The scale of the node when hovered
     */
    public static void customizeDataNode(XYChart.Data<Number, Number> data, double baseScale, double hoverScale) {
        Node node = data.getNode();
        if (node != null) {
            // Set initial size of the node based on the total number of nodes
            node.setScaleX(baseScale);
            node.setScaleY(baseScale);

            // Create a timeline for smooth scaling on hover
            Timeline enlargeTimeline = new Timeline();
            Timeline shrinkTimeline = new Timeline();

            // Define the enlarge animation when the mouse enters
            enlargeTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(150),
                    new KeyValue(node.scaleXProperty(), hoverScale),
                    new KeyValue(node.scaleYProperty(), hoverScale)));

            // Define the shrink animation when the mouse exits
            shrinkTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(150),
                    new KeyValue(node.scaleXProperty(), baseScale),
                    new KeyValue(node.scaleYProperty(), baseScale)));

            // Add hover effect to increase node size when mouse enters
            node.setOnMouseEntered(event -> {
                shrinkTimeline.stop();
                enlargeTimeline.play();
            });

            // Reset node size when mouse exits
            node.setOnMouseExited(event -> {
                enlargeTimeline.stop();
                shrinkTimeline.play();
            });
        }
    }

    /**
     * Populate line chart and table with data for commodities.
     * 
     * @param priceData     the price data
     * @param dateFormatter the date formatter
     * @param selectedItems the selected commodities
     * @param tooltipLabel  the tooltip label of the comparison type
     * @param priceTable    the price table
     * @param lineChart     the line chart
     */
    public static void populateChart(Map<String, List<XYChart.Data<Number, Number>>> priceData,
            DateTimeFormatter dateFormatter, List<String> selectedItems, String tooltipLabel,
            TableView<Map<String, Object>> priceTable, LineChart<Number, Number> lineChart) {

        for (String item : selectedItems) {
            List<XYChart.Data<Number, Number>> prices = priceData.get(item);

            // Create series for each commodity and its price data and add to the line chart
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(item);
            series.getData().addAll(prices);
            lineChart.getData().add(series);

            // Add data to the price table
            for (XYChart.Data<Number, Number> data : prices) {
                Map<String, Object> row = new HashMap<>();
                // Convert milliseconds to LocalDate
                long milliseconds = data.getXValue().longValue();
                LocalDate date = Instant.ofEpochMilli(milliseconds)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                row.put("date", date);
                row.put("commodity", item);
                row.put("value", data.getYValue());
                priceTable.getItems().add(row);

                // Add tooltips to line chart data points
                ChartUtils.setLineChartTooltip(item, date, tooltipLabel, data, dateFormatter);

                // Determine base scale based on the number of nodes
                double baseScale = (prices.size() >= 180) ? 0.7 : (prices.size() >= 90) ? 0.75 : 0.8;
                double hoverScale = 1.8;

                // Customize the data node in the line chart to scale on hover
                ChartUtils.customizeDataNode(data, baseScale, hoverScale);
            }
        }
    }

    /**
     * Populate pie chart with volume data for cryptocurrencies.
     * 
     * @param volumes       the volume data
     * @param selectedItems the selected cryptocurrencies
     * @param pieChart      the pie chart
     */
    public static void populatePieChart(Map<String, List<XYChart.Data<Number, Number>>> volumes,
            List<String> selectedItems, PieChart pieChart) {

        List<PieChart.Data> pieData = new ArrayList<>();

        // Calculate total volume for cryptocurrencies and make slices for the pie chart
        for (String item : selectedItems) {
            List<XYChart.Data<Number, Number>> vols = volumes.get(item);

            double sumYValues = vols.stream().mapToDouble(data -> data.getYValue().doubleValue()).sum();

            PieChart.Data slice = new PieChart.Data(item, sumYValues);
            pieData.add(slice);
        }

        // Add the slices to the pie chart
        pieChart.getData().addAll(pieData);

        // Add tooltips to the slices
        for (PieChart.Data data : pieChart.getData()) {
            ChartUtils.setPieSliceTooltip(data);
        }

        // Add tooltips to pie chart labels
        ChartUtils.setPieLabelTooltips(pieChart);
    }

    /**
     * Add tooltip to line chart data point.
     * 
     * @param item          the item name
     * @param date          the date
     * @param tooltipLabel  the tooltip label of the comparison type
     * @param data          the data point
     * @param dateFormatter the date formatter
     */
    public static void setLineChartTooltip(String item, LocalDate date, String tooltipLabel,
            XYChart.Data<Number, Number> data, DateTimeFormatter dateFormatter) {

        Tooltip tooltip = new Tooltip(
                String.format("%s\nDate: %s\n%s: %s", item, date.format(dateFormatter),
                        tooltipLabel, NumberFormatter.formatNumber(data.getYValue().doubleValue(), true)));

        tooltip.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
        tooltip.setShowDelay(Duration.millis(0));
        tooltip.setHideDelay(Duration.millis(0));
        Tooltip.install(data.getNode(), tooltip);
    }

    /**
     * Add tooltip to pie chart slice.
     * 
     * @param data the pie chart data
     */
    public static void setPieSliceTooltip(PieChart.Data data) {
        Tooltip tooltip = new Tooltip(String.format("%s\nVolume: %s", data.getName(),
                NumberFormatter.formatNumber(data.getPieValue(), true)));

        tooltip.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
        tooltip.setShowDelay(Duration.millis(0));
        tooltip.setHideDelay(Duration.millis(0));
        Tooltip.install(data.getNode(), tooltip);
    }

    /**
     * Add tooltips to pie chart labels.
     * 
     * @param pieChart the pie chart
     */
    public static void setPieLabelTooltips(PieChart pieChart) {
        // Add tooltips to pie chart labels by looking up the chart legend items
        for (Node node : pieChart.lookupAll(".chart-legend-item")) {
            // Get the label text and find the corresponding data in the pie chart
            if (node instanceof Label label) {
                String itemName = label.getText();
                double itemValue = pieChart.getData().stream()
                        .filter(data -> data.getName().equals(itemName))
                        .mapToDouble(PieChart.Data::getPieValue)
                        .sum();
                Tooltip tooltip = new Tooltip(String.format("%s\nVolume: %s", itemName,
                        NumberFormatter.formatNumber(itemValue, true)));

                tooltip.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");
                tooltip.setShowDelay(Duration.millis(0));
                tooltip.setHideDelay(Duration.millis(0));
                Tooltip.install(label, tooltip);
            }
        }
    }

    /**
     * Set the axis label based on the selected comparison type and currency.
     *
     * @param axis             the axis to set the label for
     * @param comparisonType   the selected comparison type (e.g., "Price",
     *                         "%-Change", "Volume")
     * @param exchangeCurrency the currency code (e.g., "USD", "EUR") used for price
     *                         comparison
     */
    public static void setAxisLabel(NumberAxis axis, String comparisonType, String exchangeCurrency) {
        String axisLabel = switch (comparisonType) {
            case "Price" -> "Price (" + exchangeCurrency + ")";
            case "%-Change" -> "%-Change";
            case "Volume" -> "Volume";
            default -> "";
        };
        axis.setLabel(axisLabel);
    }

    /**
     * Update the x-axis date range based on the selected date range.
     * Set the tick unit based on the total days in the selected date range.
     *
     * @param xAxis     the x-axis
     * @param yAxis     the y-axis
     * @param startDate the start date of the selected date range
     * @param endDate   the end date of the selected date range
     */
    public static void updateAxisDateRange(NumberAxis xAxis, NumberAxis yAxis, LocalDate startDate, LocalDate endDate) {
        // Show the tick marks and labels of the axises
        setAxisTicksAndLabelsVisible(xAxis, true);
        setAxisTicksAndLabelsVisible(yAxis, true);

        int totalDays = (int) (endDate.toEpochDay() - startDate.toEpochDay());

        long startEpochMillis = startDate.toEpochDay() * Constants.DAY;
        long endEpochMillis = endDate.toEpochDay() * Constants.DAY;

        // Set x-axis range to the selected date range
        xAxis.setLowerBound(startEpochMillis);
        xAxis.setUpperBound(endEpochMillis);

        // Dynamically set tick unit based on the date range
        xAxis.setTickUnit(ChartUtils.getTickUnit(totalDays));
    }

    /**
     * Hide or show the tick marks and labels of the axis.
     * 
     * @param axis    the axis to set the visibility for
     * @param visible false to hide the tick marks and labels, true to show them
     */
    public static void setAxisTicksAndLabelsVisible(NumberAxis axis, boolean visible) {
        axis.setTickMarkVisible(visible);
        axis.setTickLabelsVisible(visible);
    }
}
