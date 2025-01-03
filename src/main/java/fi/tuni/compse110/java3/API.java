package fi.tuni.compse110.java3;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.tuni.compse110.java3.data.CryptoHistoryEntry;
import fi.tuni.compse110.java3.data.MetalCurrentData;
import fi.tuni.compse110.java3.data.MetalHistoryEntryAll;
import fi.tuni.compse110.java3.io.CachedRequest;
import fi.tuni.compse110.java3.io.CachedResponse;
import fi.tuni.compse110.java3.utility.Constants;
import fi.tuni.compse110.java3.utility.DialogUtils;
import fi.tuni.compse110.java3.utility.HistoryChunk;
import fi.tuni.compse110.java3.utility.Parser;
import javafx.scene.chart.XYChart;

/**
 * Handles getting data for the controller using other components.
 */
public class API {

    // Fetch list of available cryptocurrencies from API and store it in cache.
    static {
        try {
            CachedResponse response = CachedRequest.get(
                    "https://api.coingecko.com/api/v3/coins/list?x_cg_demo_api_key=" + Constants.API_KEY_CRYPTOS,
                    24 * 60 * 60);

            System.out.println((System.currentTimeMillis() - response.getTime()) / 1000 +
                    " seconds ago: " + response.getResponse());

            AVAILABLE_CRYPTOS_CACHE = Parser.parseCoinsList(response.getResponse());
        } catch (Exception ex) {
            DialogUtils.showErrorDialog("Failed to fetch available cryptocurrencies", ex);
        }
    }

    /**
     * Available crypto names.
     */
    private static final List<String> CRYPTOCURRENCIES = new ArrayList<>(Constants.DEFAULT_CRYPTOCURRENCIES);

    /**
     * Available metal names.
     */
    private static final List<String> METALS = new ArrayList<>(Constants.DEFAULT_METALS);

    /**
     * Available currency names.
     */
    private static final List<String> CURRENCIES = new ArrayList<>(Constants.DEFAULT_CURRENCIES);
    /**
     * Date format used by metals.dev API.
     */
    private static final DateTimeFormatter METAL_API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    /**
     * Stores already fetched and parsed history data for cryptos.
     */
    private static final Map<String, List<CryptoHistoryEntry>> CRYPTO_HISTORY_CACHE = new HashMap<>();

    /**
     * Stores already fetched and parsed history data for metals.
     */
    private static final Map<Integer, List<MetalHistoryEntryAll>> METAL_HISTORY_CACHE = new HashMap<>();

    /**
     * Stores already fetched and parsed exchange rates related to USD.
     */
    private static final Map<String, Double> EXCHANGE_RATE_CACHE = new HashMap<>();

    /**
     * Stores already fetched and parsed available cryptos list.
     */
    private static Map<String, String> AVAILABLE_CRYPTOS_CACHE;

    /**
     * Get available crypto names.
     * 
     * @return list of available crypto names
     */
    public static List<String> getAvailableCryptocurrencies() {
        return CRYPTOCURRENCIES;
    }

    /**
     * Get all available crypto names from API.
     * 
     * @return list of all available crypto names
     */
    public static List<String> getAllAvailableCryptocurrencies() {
        return new ArrayList<>(AVAILABLE_CRYPTOS_CACHE.keySet());
    }

    /**
     * Add available crypto name.
     * 
     * @param cryptocurrency crypto name to add
     */
    public static void addAvailableCryptocurrency(String cryptocurrency) {
        if (!CRYPTOCURRENCIES.contains(cryptocurrency)) {
            CRYPTOCURRENCIES.add(cryptocurrency);
        }
    }

    /**
     * Reset available crypto names to the default list.
     */
    public static void resetCryptocurrencies() {
        CRYPTOCURRENCIES.clear();
        CRYPTOCURRENCIES.addAll(Constants.DEFAULT_CRYPTOCURRENCIES);
    }

    /**
     * Get available metal names.
     * 
     * @return list of available metal names
     */
    public static List<String> getAvailableMetals() {
        return METALS;
    }

    /**
     * Get available currency names.
     * 
     * @return list of available currency names
     */
    public static List<String> getAvailableCurrencies() {
        return CURRENCIES;
    }

    /**
     * Get crypto prices/volumes chart data for selected cryptocurrencies.
     * Note: Can't go to history past 365 days, a limit by free CoinGecko API.
     * 
     * @param currencies       selected cryptocurrencies
     * @param startDate        start date
     * @param endDate          end data
     * @param exchangeCurrency currency to exchange to
     * @param getVolume        true if volume data is retrieved, false for price
     *                         data
     * @return map containing price chart xy values for each crypto
     */
    public static Map<String, List<XYChart.Data<Number, Number>>> getCryptos(List<String> currencies,
            LocalDate startDate, LocalDate endDate, String exchangeCurrency, boolean getVolume) {

        Map<String, List<XYChart.Data<Number, Number>>> chartData = new HashMap<>();
        Double exchangeRate = getExchangeRate(exchangeCurrency);

        if (currencies == null || currencies.isEmpty() || exchangeRate == null) {
            return chartData;
        }

        long start = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        long end = endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;

        for (String currency : currencies) {
            // Check if already parsed history data is available in cache
            List<CryptoHistoryEntry> parsedHistory = CRYPTO_HISTORY_CACHE.get(currency);

            if (parsedHistory == null) {
                try {
                    CachedResponse cryptoHistory = CachedRequest.get(
                            "https://api.coingecko.com/api/v3/coins/" + AVAILABLE_CRYPTOS_CACHE.get(currency) +
                                    "/market_chart?vs_currency=usd&days=365&interval=daily&x_cg_demo_api_key="
                                    + Constants.API_KEY_CRYPTOS,
                            12 * 60 * 60);

                    System.out.println((System.currentTimeMillis() - cryptoHistory.getTime()) / 1000 + " seconds ago: "
                            + cryptoHistory.getResponse());

                    parsedHistory = Parser.parseCryptoHistoryData(cryptoHistory.getResponse());

                    CRYPTO_HISTORY_CACHE.put(currency, parsedHistory);
                } catch (Exception ex) {
                    DialogUtils.showErrorDialog("Failed to get data for " + currency, ex);
                    return chartData;
                }
            }

            List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();

            for (CryptoHistoryEntry entry : parsedHistory) {
                if (entry.getTimestamp() < start || entry.getTimestamp() > end) {
                    continue;
                }

                dataList.add(
                        new XYChart.Data<>(entry.getTimestamp(),
                                getVolume ? entry.getVolume() : entry.getPrice() / exchangeRate));
            }

            if (dataList.isEmpty()) {
                DialogUtils.showWarningDialog("Not Found", "No data found for selected interval for " + currency);
            } else {
                chartData.put(currency, dataList);
            }
        }

        return chartData;
    }

    /**
     * Get crypto prices chart data for selected metals.
     *
     * @param metals           selected metals
     * @param startDate        start date
     * @param endDate          end data
     * @param exchangeCurrency currency to exchange to
     * @return map containing price chart xy values for each metal
     */
    public static Map<String, List<XYChart.Data<Number, Number>>> getMetals(List<String> metals, LocalDate startDate,
            LocalDate endDate, String exchangeCurrency) {

        Map<String, List<XYChart.Data<Number, Number>>> chartData = new HashMap<>();
        Double exchangeRate = getExchangeRate(exchangeCurrency);

        if (metals == null || metals.isEmpty() || exchangeRate == null) {
            return chartData;
        }

        // Always get full 30 days of history and filter that to avoid different API
        // call for each possible selection.
        // Already parsed history is stored in METAL_HISTORY_CACHE.
        // Also, the history data doesn't include current day so we must use a different
        // API call for that.

        LocalDate today = LocalDate.now();
        long start = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        long end = endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
        int startChunk = HistoryChunk.getChunkIndex(startDate, 30);
        int endChunk = HistoryChunk.getChunkIndex(endDate, 30);

        // Check if today is the only day in endChunk (today is handled via separate API call)
        if (endDate.isEqual(today) && today.equals(HistoryChunk.getChunkStart(endChunk, 30))) {
            endChunk--;
        }

        ArrayList<MetalHistoryEntryAll> parsed = new ArrayList<>();

        if (endDate.isEqual(today)) {
            // Check if already parsed history data is available in cache
            List<MetalHistoryEntryAll> parsedHistory = METAL_HISTORY_CACHE.get(Integer.MAX_VALUE);

            if (parsedHistory == null) {
                try {
                    CachedResponse metalHistory = CachedRequest.get(
                            "https://api.metals.dev/v1/latest?api_key=" + Constants.API_KEY_METALS,
                            12 * 60 * 60);

                    System.out.println((System.currentTimeMillis() - metalHistory.getTime()) / 1000 + " seconds ago: "
                            + metalHistory.getResponse());

                    MetalCurrentData parsedCurrent = Parser.parseMetalCurrentData(metalHistory.getResponse());

                    parsedHistory = List
                            .of(new MetalHistoryEntryAll(today.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000,
                                    parsedCurrent.getCurrentMetals()));

                    METAL_HISTORY_CACHE.put(Integer.MAX_VALUE, parsedHistory);
                } catch (Exception ex) {
                    DialogUtils.showErrorDialog("Failed to get data for metals", ex);
                    return chartData;
                }
            }

            parsed.addAll(parsedHistory);
        }

        for (int chunk = startChunk; chunk <= endChunk; chunk++) {
            LocalDate chunkStart = HistoryChunk.getChunkStart(chunk, 30);
            LocalDate chunkEnd = HistoryChunk.getChunkEnd(chunk, 30);

            List<MetalHistoryEntryAll> parsedHistory = METAL_HISTORY_CACHE.get(chunk);

            if (parsedHistory == null) {
                CachedResponse metalHistory;
                try {
                    LocalDate yesterday = today.minusDays(1);
                    LocalDate adjustedChunkEnd = chunkEnd;

                    // In end chunk, make sure we won't request dates in the future
                    // or today which is handled via separate API call
                    if (chunk == endChunk) {
                        if (adjustedChunkEnd.isAfter(yesterday)) {
                            adjustedChunkEnd = yesterday;
                        }
                    }

                    metalHistory = CachedRequest.get(
                            "https://api.metals.dev/v1/timeseries?api_key=" + Constants.API_KEY_METALS +
                                    "&start_date=" + METAL_API_DATE_FORMAT.format(chunkStart) +
                                    "&end_date=" + METAL_API_DATE_FORMAT.format(adjustedChunkEnd),
                            // No need for full 30 days chunks of history data only to expire
                            chunkEnd.isBefore(yesterday) ? Integer.MAX_VALUE : 12 * 60 * 60);

                    System.out.println((System.currentTimeMillis() - metalHistory.getTime()) / 1000 + " seconds ago: "
                            + metalHistory.getResponse());

                    parsedHistory = Parser.parseMetalHistoryData(metalHistory.getResponse());

                    METAL_HISTORY_CACHE.put(chunk, parsedHistory);
                } catch (Exception ex) {
                    DialogUtils.showErrorDialog("Failed to get data for metals", ex);
                    return chartData;
                }
            }

            parsed.addAll(parsedHistory);
        }

        for (String metal : metals) {
            addMetal(metal, parsed, chartData, start, end, exchangeRate);
        }

        return chartData;
    }

    /**
     * Creates chart data for selected metal.
     *
     * @param metal        the name of the metal (e.g., "Gold", "Palladium", "Platinum", "Silver")
     * @param parsed       the list of parsed metal history entries
     * @param dataMap      the map to store the chart data
     * @param start        the start timestamp for the data range
     * @param end          the end timestamp for the data range
     * @param exchangeRate the exchange rate to convert the metal prices
     */
    private static void addMetal(String metal, ArrayList<MetalHistoryEntryAll> parsed,
            Map<String, List<XYChart.Data<Number, Number>>> dataMap, long start, long end, double exchangeRate) {

        List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();

        for (MetalHistoryEntryAll entry : parsed) {
            // Skip entries outside the selected date range
            if (entry.getTimestamp() < start || entry.getTimestamp() > end) {
                continue;
            }
            // Get the price for the selected metal
            double price = switch (metal) {
                case "Gold" -> entry.getMetalData().getGold();
                case "Palladium" -> entry.getMetalData().getPalladium();
                case "Platinum" -> entry.getMetalData().getPlatinum();
                case "Silver" -> entry.getMetalData().getSilver();
                default -> throw new IllegalArgumentException();
            };

            dataList.add(new XYChart.Data<>(entry.getTimestamp(), price / exchangeRate));
        }

        if (dataList.isEmpty()) {
            DialogUtils.showWarningDialog("Not Found", "No data found for selected interval for " + metal);
        }
        else {
            dataMap.put(metal, dataList);
        }
    }

    /**
     * Get exchange rate for given currency. USD is the base currency.
     *
     * @param currency the currency code (e.g., "USD", "EUR")
     * @return the exchange rate for the given currency, or null if not available
     */
    private static Double getExchangeRate(String currency) {
        if ("USD".equals(currency)) {
            return 1D;
        }
        // Fetch exchange rates from API and store them in cache if not already fetched
        if (!EXCHANGE_RATE_CACHE.containsKey(currency)) {
            try {
                CachedResponse exchangeRatesResponse = CachedRequest.get(
                        "https://api.metals.dev/v1/currencies?api_key=" + Constants.API_KEY_METALS + "&base=USD",
                        12 * 60 * 60);

                System.out.println((System.currentTimeMillis() - exchangeRatesResponse.getTime()) / 1000 +
                        " seconds ago: " + exchangeRatesResponse.getResponse());

                EXCHANGE_RATE_CACHE
                        .putAll(Parser.parseExchangeRateData(exchangeRatesResponse.getResponse(), CURRENCIES));
            } catch (Exception ex) {
                DialogUtils.showErrorDialog("Failed to fetch currency exchange rates", ex);
                return null;
            }
        }
        // Get exchange rate for the given currency
        Double rate = EXCHANGE_RATE_CACHE.get(currency);

        if (rate == null) {
            DialogUtils.showWarningDialog("Failed", "Exchange rate for " + currency + " not available");
        }

        return rate;
    }
}
