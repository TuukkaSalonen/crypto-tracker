package fi.tuni.compse110.java3.utility;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

/**
 * Basic class for handling the bounds of the line chart.
 */
public class ChartBounds {

    private final double maxValue;
    private final double minValue;

    private double lowerBoundAnchorY, upperBoundAnchorY;
    private long lowerBoundAnchorX, upperBoundAnchorX;
    private final List<Long> xAxisPoints;
    private final LineChart<Number, Number> lineChart;
    private final NumberAxis yAxis;
    private final NumberAxis xAxis;
    private final Bounds plotBounds;

    /**
     * Constructor for the ChartBounds class. Initializes the bounds of the chart.
     * 
     * @param chart    The line chart
     * @param plotArea The plot area of the chart
     */
    public ChartBounds(LineChart<Number, Number> chart, Node plotArea) {
        this.maxValue = ChartUtils.calculateMaxValueY(chart);
        this.minValue = ChartUtils.calculateMinValueY(chart);

        this.lineChart = chart;
        this.xAxis = (NumberAxis) chart.getXAxis();
        this.yAxis = (NumberAxis) chart.getYAxis();
        this.plotBounds = plotArea.getBoundsInLocal();

        this.xAxisPoints = lineChart.getData().stream()
                .flatMap(series -> series.getData().stream())
                .map(data -> data.getXValue().longValue())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        this.upperBoundAnchorX = xAxisPoints.get(xAxisPoints.size() - 1);
        this.lowerBoundAnchorX = xAxisPoints.get(0);
    }

    /**
     * Zooms the x-axis of the chart by changing the lower and upper bounds of the x-axis.
     * 
     * @param mouseX     The x-coordinate of the mouse
     * @param zoomFactor The factor by which to zoom (in/out)
     */
    public void zoomXAxis(double mouseX, double zoomFactor) {
        double plotWidth = plotBounds.getWidth();

        double currentLowerBoundX = lowerBoundAnchorX;
        double currentUpperBoundX = upperBoundAnchorX;
        double currentRangeX = currentUpperBoundX - currentLowerBoundX;

        double mousePositionRatioX = mouseX / plotWidth;

        // Calculate the new bounds based on the zoom factor and mouse position
        double newLowerBoundX = currentLowerBoundX + (currentRangeX * (1 - zoomFactor)) * mousePositionRatioX;
        double newUpperBoundX = currentUpperBoundX - (currentRangeX * (1 - zoomFactor)) * (1 - mousePositionRatioX);

        // Ensure that the new bounds are within the range of the x-axis points
        newLowerBoundX = Math.max(xAxisPoints.get(0), newLowerBoundX);
        newUpperBoundX = Math.min(xAxisPoints.get(xAxisPoints.size() - 1), newUpperBoundX);

        if (newUpperBoundX - newLowerBoundX < Constants.DAY) {
            double midPointX = (newUpperBoundX + newLowerBoundX) / 2;
            newLowerBoundX = midPointX - Constants.DAY / 2;
            newUpperBoundX = midPointX + Constants.DAY / 2;
        }

        // Check that x-axis doesnt go out of bounds
        if (newLowerBoundX < xAxisPoints.get(0)) {
            newLowerBoundX = xAxisPoints.get(0);
            newUpperBoundX = newLowerBoundX + currentRangeX;
        }

        // Check that x-axis doesnt go out of bounds
        if (newUpperBoundX > xAxisPoints.get(xAxisPoints.size() - 1)) {
            newUpperBoundX = xAxisPoints.get(xAxisPoints.size() - 1);
            newLowerBoundX = newUpperBoundX - currentRangeX;
        }

        // Adjust the x-axis bounds to reflect the new bounds
        if (newLowerBoundX < newUpperBoundX) {
            xAxis.setAutoRanging(false);
            ChartUtils.updateAxisDateRange(xAxis, yAxis, LocalDate.ofEpochDay((long) (newLowerBoundX / Constants.DAY)),
                    LocalDate.ofEpochDay((long) (newUpperBoundX / Constants.DAY)));
        } else {
            // Reset the bounds to auto-ranging if zooming invalid
            xAxis.setAutoRanging(true);
        }

        // Update the anchor bounds to the new bounds
        lowerBoundAnchorX = (long) newLowerBoundX;
        upperBoundAnchorX = (long) newUpperBoundX;
    }

    /**
     * Zooms the y-axis of the chart by changing the lower and upper bounds of the y-axis.
     * 
     * @param mouseY     The y-coordinate of the mouse
     * @param zoomFactor The factor by which to zoom (in/out)
     */
    public void zoomYAxis(double mouseY, double zoomFactor) {
        double plotHeight = plotBounds.getHeight();

        double currentLowerBoundY = yAxis.getLowerBound();
        double currentUpperBoundY = yAxis.getUpperBound();
        double currentRangeY = currentUpperBoundY - currentLowerBoundY;

        double mousePositionRatioY = 1 - (mouseY / plotHeight);

        // Calculate the new bounds based on the zoom factor and mouse position
        double newLowerBoundY = currentLowerBoundY + (currentRangeY * (1 - zoomFactor)) * mousePositionRatioY;
        double newUpperBoundY = currentUpperBoundY - (currentRangeY * (1 - zoomFactor)) * (1 - mousePositionRatioY);

        // Ensure that the new bounds are within the range of the y-axis points with some padding
        newLowerBoundY = Math.max(minValue < 0 ? minValue * 1.1 : 0, newLowerBoundY);
        newUpperBoundY = Math.min(maxValue * 1.1, newUpperBoundY);

        // Handle negative lower bound values correctly
        if (newLowerBoundY < 0) {
            newLowerBoundY = ChartUtils.floorChartValue(newLowerBoundY);
        } else {
            newLowerBoundY = ChartUtils.roundChartValue(newLowerBoundY);
        }

        newUpperBoundY = ChartUtils.roundChartValue(newUpperBoundY);

        // Increase upperbound more when new bound small compared to largest value
        if ((newLowerBoundY == 0 && newUpperBoundY < maxValue / 10) && zoomFactor > 1) {
            newUpperBoundY = newUpperBoundY * 10;
        }

        // Decrease lowerbound more when lowerbound is close to the upperbound
        if (newUpperBoundY - newLowerBoundY < newUpperBoundY / 20) {
            newLowerBoundY = newLowerBoundY * 0.9;
        }

        // Zoom out more if the new calculated bounds are the same as the current bounds
        if (newLowerBoundY == currentLowerBoundY && newUpperBoundY == currentUpperBoundY && zoomFactor > 1) {
            if (newUpperBoundY < maxValue) {
                newUpperBoundY *= 1.5;
            }
            if (newLowerBoundY > minValue) {
                newLowerBoundY *= 0.9;
            }
        }

        // Ensure that the lower bound is less than the upper bound
        if (newLowerBoundY < newUpperBoundY) {
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(newLowerBoundY);
            yAxis.setUpperBound(newUpperBoundY);

            // Adjust the tick unit based on the new range
            double newRangeY = newUpperBoundY - newLowerBoundY;
            double tickUnitY = ChartUtils.calculateYTickUnit(newRangeY);
            yAxis.setTickUnit(tickUnitY);
        } else {
            // Reset the bounds to auto-ranging if zooming invalid
            yAxis.setAutoRanging(true);
        }
        // Update the anchor bounds to the new bounds
        lowerBoundAnchorY = newLowerBoundY;
        upperBoundAnchorY = newUpperBoundY;
    }

    /**
     * Checks if the chart is fully zoomed out. Resets zoom if it is.
     */
    public void checkZoomReset() {
        if ((lowerBoundAnchorY == ChartUtils.floorChartValue(minValue * 1.1) || lowerBoundAnchorY == 0)
                && upperBoundAnchorY == ChartUtils.roundChartValue(maxValue * 1.1) &&
                lowerBoundAnchorX == xAxisPoints.get(0)
                && upperBoundAnchorX == xAxisPoints.get(xAxisPoints.size() - 1)) {
            resetChartZoom();
        }
    }

    /**
     * Resets the zoom of the chart.
     */
    public void resetChartZoom() {
        yAxis.setAutoRanging(true);
        xAxis.setAutoRanging(false);

        // Reset the y-axis bounds to the default values
        lowerBoundAnchorX = xAxisPoints.get(0);
        upperBoundAnchorX = xAxisPoints.get(xAxisPoints.size() - 1);

        xAxis.setLowerBound(lowerBoundAnchorX);
        xAxis.setUpperBound(upperBoundAnchorX);
    }

    /**
     * Sets the chart current anchors as pressed.
     */
    public void setChartPressed() {
        lowerBoundAnchorY = yAxis.getLowerBound();
        upperBoundAnchorY = yAxis.getUpperBound();
        lowerBoundAnchorX = (long) xAxis.getLowerBound();
        upperBoundAnchorX = (long) xAxis.getUpperBound();
    }

    /**
     * Returns whether all nodes are visible in the chart.
     *
     * @return true if all nodes are visible, false otherwise
     */
    public boolean allNodesVisible() {
        return (yAxis.getUpperBound() > maxValue &&
                yAxis.getLowerBound() < minValue &&
                lowerBoundAnchorX == xAxisPoints.get(0)
                && upperBoundAnchorX == xAxisPoints.get(xAxisPoints.size() - 1));
    }

    /**
     * Pans the y-axis of the chart by the specified delta value.
     * 
     * @param deltaY the delta value to pan the y-axis by
     */
    public void dragYAxis(double deltaY) {
        double rangeY = upperBoundAnchorY - lowerBoundAnchorY;
        double deltaValueY = -(deltaY / lineChart.getHeight()) * rangeY * Constants.Y_AXIS_DRAG_SCALE;

        double newLowerBoundY = lowerBoundAnchorY - deltaValueY;
        double newUpperBoundY = upperBoundAnchorY - deltaValueY;

        // Ensure that the new bounds are within the range of the y-axis points with some padding
        if (newLowerBoundY < 0) {
            newLowerBoundY = ChartUtils.floorChartValue(Math.max(minValue * 1.1, newLowerBoundY));
        } else {
            newLowerBoundY = ChartUtils.roundChartValue(Math.max(0, newLowerBoundY));
        }
        newUpperBoundY = ChartUtils.roundChartValue(Math.min(maxValue * 1.1, newUpperBoundY));

        // Prevent panning from hiding nodes and keep the range the same
        if (newLowerBoundY < newUpperBoundY
                && (newLowerBoundY >= 0 || (newLowerBoundY < 0 && newLowerBoundY >= minValue * 1.1))
                && newUpperBoundY <= maxValue * 1.1) {
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(newLowerBoundY);
            yAxis.setUpperBound(newUpperBoundY);
            // Adjust the tick unit based on the new range
            double newRangeY = newUpperBoundY - newLowerBoundY;
            double tickUnitY = ChartUtils.calculateYTickUnit(newRangeY);
            yAxis.setTickUnit(tickUnitY);
        }
    }

    /**
     * Pans the x-axis of the chart by the specified delta value.
     * 
     * @param deltaX the delta value to pan the x-axis by
     */
    public void dragXAxis(double deltaX) {
        double rangeX = upperBoundAnchorX - lowerBoundAnchorX;
        double deltaValueX = (deltaX / lineChart.getWidth()) * rangeX * Constants.X_AXIS_DRAG_SCALE;

        double newLowerBoundX = lowerBoundAnchorX - deltaValueX;
        double newUpperBoundX = upperBoundAnchorX - deltaValueX;

        // Ensure that the new bounds are within the range of the x-axis points
        newLowerBoundX = Math.max(xAxisPoints.get(0), (long) newLowerBoundX);
        newUpperBoundX = Math.min(xAxisPoints.get(xAxisPoints.size() - 1), (long) newUpperBoundX);

        if (newLowerBoundX < newUpperBoundX && newLowerBoundX >= xAxisPoints.get(0)
                && newUpperBoundX <= xAxisPoints.get(xAxisPoints.size() - 1)) {
            // Prevent updating bounds if either limit is reached
            if (newLowerBoundX == xAxisPoints.get(0)
                    || newUpperBoundX == xAxisPoints.get(xAxisPoints.size() - 1)) {
                newLowerBoundX = lowerBoundAnchorX;
                newUpperBoundX = upperBoundAnchorX;
            }

            xAxis.setAutoRanging(false);
            ChartUtils.updateAxisDateRange(xAxis, yAxis, LocalDate.ofEpochDay((long) (newLowerBoundX / Constants.DAY)),
                    LocalDate.ofEpochDay((long) (newUpperBoundX / Constants.DAY)));

            // Update the anchor bounds to the new bounds
            lowerBoundAnchorX = (long) newLowerBoundX;
            upperBoundAnchorX = (long) newUpperBoundX;
        }
    }
}