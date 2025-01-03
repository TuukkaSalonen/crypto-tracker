package fi.tuni.compse110.java3.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ChartUtilsTest {
 
    @Test
    public void testRoundChartValue() {
        assertEquals(1.0, ChartUtils.roundChartValue(1.2));
        assertEquals(15.0, ChartUtils.roundChartValue(14.5));
        assertEquals(0.1, ChartUtils.roundChartValue(0.100));
        assertEquals(0.01, ChartUtils.roundChartValue(0.01));
    }

    @Test
    public void testFloorChartValue() {
        assertEquals(-1.0, ChartUtils.floorChartValue(-0.95));
        assertEquals(-0.2, ChartUtils.floorChartValue(-0.195));
        assertEquals(-0.05, ChartUtils.floorChartValue(-0.0495));
        assertEquals(-0.008, ChartUtils.floorChartValue(-0.00795));
    }

    @Test
    public void testCalculateYTickUnit() {
        assertEquals(0.1, ChartUtils.calculateYTickUnit(0.5));
        assertEquals(1, ChartUtils.calculateYTickUnit(5));
        assertEquals(10, ChartUtils.calculateYTickUnit(50));
        assertEquals(100, ChartUtils.calculateYTickUnit(500));
        assertEquals(500, ChartUtils.calculateYTickUnit(6000));
        assertEquals(1000, ChartUtils.calculateYTickUnit(15000));
    }

    @Test
    public void testGetTickUnit() {
        assertEquals(Constants.DAY, ChartUtils.getTickUnit(7)); // 1 day
        assertEquals(Constants.DAY * 2, ChartUtils.getTickUnit(14)); // 2 days
        assertEquals(Constants.DAY * 3, ChartUtils.getTickUnit(30)); // 3 days
        assertEquals(Constants.DAY * 7, ChartUtils.getTickUnit(90)); // 7 days
        assertEquals(Constants.DAY * 14, ChartUtils.getTickUnit(180)); // 14 days
        assertEquals(Constants.DAY * 30, ChartUtils.getTickUnit(365)); // 30 days
    }

    @Test
    public void testRoundTickValue() {
        assertEquals(0.5, ChartUtils.roundTickValue(0.5));
        assertEquals(2.0, ChartUtils.roundTickValue(1.5));
        assertEquals(5.0, ChartUtils.roundTickValue(3.0));
        assertEquals(10.0, ChartUtils.roundTickValue(7.0));
        assertEquals(20.0, ChartUtils.roundTickValue(15.0));
        assertEquals(50.0, ChartUtils.roundTickValue(30.0));
        assertEquals(100.0, ChartUtils.roundTickValue(70.0));
        assertEquals(200.0, ChartUtils.roundTickValue(150.0));
        assertEquals(500.0, ChartUtils.roundTickValue(300.0));
        assertEquals(1000.0, ChartUtils.roundTickValue(700.0));
    }
}