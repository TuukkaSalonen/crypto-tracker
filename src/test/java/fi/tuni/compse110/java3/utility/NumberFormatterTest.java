package fi.tuni.compse110.java3.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NumberFormatterTest {

    private static final String decimalSeparator = ".";

    @Test
    public void testFormatNumberWithBillions() {
        assertEquals("1" + decimalSeparator + "00B", formatNumber(1_000_000_000, true));
        assertEquals("1B", formatNumber(1_000_000_000, false));
    }

    @Test
    public void testFormatNumberWithMillions() {
        assertEquals("1" + decimalSeparator + "00M", formatNumber(1_000_000, true));
        assertEquals("1M", formatNumber(1_000_000, false));
    }

    @Test
    public void testFormatNumberWithThousands() {
        assertEquals("1234" + decimalSeparator + "57", formatNumber(1234.567, true));
        assertEquals("1235", formatNumber(1234.567, false));
    }

    @Test
    public void testFormatNumberWithZero() {
        assertEquals("0" + decimalSeparator + "00", formatNumber(0, true));
        assertEquals("0", formatNumber(0, false));
    }

    @Test
    public void testFormatNumberWithSmallNumbers() {
        assertEquals("5" + decimalSeparator + "00", formatNumber(5, true));
        assertEquals("5" + decimalSeparator + "00", formatNumber(5, false));
        assertEquals("10", formatNumber(10, false));
    }

    @Test
    public void testFormatNumberWithVerySmallNumbers() {
        assertEquals("0" + decimalSeparator + "005", formatNumber(0.005d, true));
        assertEquals("0" + decimalSeparator + "0005", formatNumber(0.0005d, true));
        assertEquals("0" + decimalSeparator + "00005", formatNumber(0.00005d, true));
        assertEquals("0" + decimalSeparator + "000005", formatNumber(0.000005d, true));
        assertEquals("0" + decimalSeparator + "0000005", formatNumber(0.0000005d, true));
        assertEquals("0" + decimalSeparator + "00000005", formatNumber(0.00000005d, true));
        assertEquals("0" + decimalSeparator + "00000006", formatNumber(0.000000055d, true));
        assertEquals("0" + decimalSeparator + "00000005", formatNumber(0.000000054d, true));
    }

    @Test
    public void testFormatNumberWithNegativeNumbers() {
        assertEquals("-5" + decimalSeparator + "00", formatNumber(-5, false));
        assertEquals("-10", formatNumber(-10, false));
        assertEquals("-10" + decimalSeparator + "10", formatNumber(-10.1, true));
        assertEquals("-10", formatNumber(-10.1, false));
        assertEquals("-123457", formatNumber(-123456.50, false));
    }

    /**
     * Uses NumberFormatter.formatNumber but forces . as decimal separator
     */
    private static String formatNumber(double value, boolean withDecimals) {
        return NumberFormatter.formatNumber(value, withDecimals).replace(",", ".");
    }
}
