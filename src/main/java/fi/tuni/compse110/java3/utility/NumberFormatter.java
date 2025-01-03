package fi.tuni.compse110.java3.utility;

/**
 * Class for formatting numbers more readable.
 */
public class NumberFormatter {

    /**
     * Format number to be more readable.
     * 
     * @param value        number to be formatted
     * @param withDecimals should the number have decimals
     * @return formatted number as String
     */
    public static String formatNumber(double value, boolean withDecimals) {
        if (value >= 1_000_000_000) {
            return String.format(withDecimals ? "%.2fB" : "%.0fB", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format(withDecimals ? "%.2fM" : "%.0fM", value / 1_000_000);
        } else if (value == 0) {
            return String.format(withDecimals ? "%.2f" : "%.0f", value);
        } else if (value > 0 && value < 0.01) {
            // Show more decimals for small numbers
            if (value < 0.0000001) return String.format("%.8f", value);
            if (value < 0.000001) return String.format("%.7f", value);
            if (value < 0.00001) return String.format("%.6f", value);
            if (value < 0.0001) return String.format("%.5f", value);
            if (value < 0.001) return String.format("%.4f", value);
            return String.format("%.3f", value);
        } else if (value > -10 && value < 10) {
            // Show decimals for numbers between -10 and 10
            return String.format("%.2f", value);
        } else {
            return String.format(withDecimals ? "%.2f" : "%.0f", value);
        }
    }
}
