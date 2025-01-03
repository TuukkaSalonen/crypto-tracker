package fi.tuni.compse110.java3.utility;

import java.util.Arrays;
import java.util.List;

/**
 * Constants used in the program.
 */
public class Constants {
    /**
     * Default cryptocurrencies
     */
    public static final List<String> DEFAULT_CRYPTOCURRENCIES = Arrays.asList(
            "Bitcoin", "Cardano", "Chainlink", "Dogecoin", "Ethereum", "Litecoin", "Monero", "Polkadot", "XRP",
            "Solana");

    /**
     * Default metals
     */
    public static final List<String> DEFAULT_METALS = Arrays.asList("Gold", "Palladium", "Platinum", "Silver");

    /**
     * Default currencies
     */
    public static final List<String> DEFAULT_CURRENCIES = Arrays.asList("AUD", "BRL", "CAD", "CHF", "CNY", "EUR", "GBP",
            "JPY", "SEK", "USD");

    /**
     * User preferences file name
     */
    public static final String USER_PREFERENCES_FILENAME = "preferences/user_preferences.json";

    /**
     * Minimum price table height
     */
    public static final int TABLE_MIN_Y_LIMIT = 200;

    /**
     * Default width for table columns
     */
    public static final double DEFAULT_TABLE_COLUMN_WIDTH = 300.0;

    /**
     * Minimum width for table columns
     */
    public static final double MIN_TABLE_COLUMN_WIDTH = 10;

    /**
     * 1 day in milliseconds
     */
    public static final long DAY = 86400000L;

    /**
     * Chart drag scale x factor
     */
    public static final double X_AXIS_DRAG_SCALE = 0.1;

    /**
     * Chart drag scale y factor
     */
    public static final double Y_AXIS_DRAG_SCALE = 0.8;

    /**
     * API key for CoinGecko
     */
    public static final String API_KEY_CRYPTOS = "CG-aZs3mmvpF3dJXDpjRuVgmmpK";

    /**
     * API key for metals.dev
     */
    public static final String API_KEY_METALS = "YNQPBWALRDNXWEBAJJXI987BAJJXI";

    /**
     * Zoom factor for zooming in
     */
    public static final double ZOOM_FACTOR_IN = 0.8;

    /**
     * Zoom factor for zooming out
     */
    public static final double ZOOM_FACTOR_OUT = 1.2;
}
