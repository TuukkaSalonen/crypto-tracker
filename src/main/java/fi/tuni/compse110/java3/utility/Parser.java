package fi.tuni.compse110.java3.utility;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import fi.tuni.compse110.java3.data.Coin;
import fi.tuni.compse110.java3.data.CryptoHistoryData;
import fi.tuni.compse110.java3.data.CryptoHistoryEntry;
import fi.tuni.compse110.java3.data.MetalCurrentData;
import fi.tuni.compse110.java3.data.MetalHistoryEntryAll;
import fi.tuni.compse110.java3.data.Metals;

/**
 * Utility class for parsing JSON data related to cryptocurrency and metal history.
 */
public class Parser {

    /**
     * Returns a list of CryptoHistoryEntry objects from the given JSON string.
     *
     * @param json the JSON string containing the cryptocurrency history data
     * @return a list of CryptoHistoryEntry objects
     */
    public static List<CryptoHistoryEntry> parseCryptoHistoryData(String json) {
        Gson gson = GsonSingleton.getGsonInstance();
        CryptoHistoryData data = gson.fromJson(json, CryptoHistoryData.class);

        ArrayList<CryptoHistoryEntry> historyPriceData = new ArrayList<>();

        List<List<Number>> priceData = data.getPriceData();
        List<List<Number>> totalVolumes = data.getTotalVolumes();

        // Combine price and volume data into CryptoHistoryEntry objects
        for (int i = 0; i < priceData.size(); ++i) {
            long timestamp = priceData.get(i).get(0).longValue();
            double price = priceData.get(i).get(1).doubleValue();
            double volume = totalVolumes.get(i).get(1).doubleValue();

            historyPriceData.add(new CryptoHistoryEntry(timestamp, price, volume));
        }

        return historyPriceData;
    }

    /**
     * Returns a list of MetalHistoryDataAll objects from the given JSON string.
     * List contains a Metals object for each date.
     *
     * @param json the JSON string containing the metal history data
     * @return a list of MetalHistoryEntryAll objects
     */
    public static List<MetalHistoryEntryAll> parseMetalHistoryData(String json) {
        Gson gson = GsonSingleton.getGsonInstance();
        ArrayList<MetalHistoryEntryAll> metalHistoryData = new ArrayList<>();

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonElement ratesElement = jsonObject.get("rates");

        // Check if rates element is present and is a JSON object, return empty list if not
        if (ratesElement == null || !ratesElement.isJsonObject()) {
            return metalHistoryData;
        }
        JsonObject ratesObject = jsonObject.getAsJsonObject("rates");

        // Convert metal price data for each date into MetalHistoryEntryAll objects
        for (Map.Entry<String, JsonElement> entry : ratesObject.entrySet()) {
            String date = entry.getKey();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate timeObject = LocalDate.parse(date, formatter);
            Instant instant = timeObject.atStartOfDay().toInstant(ZoneOffset.UTC);
            long unixTimestamp = instant.getEpochSecond() * 1000;

            JsonObject rateData = ratesObject.getAsJsonObject(date);
            Metals metalPriceData = gson.fromJson(rateData.getAsJsonObject("metals"), Metals.class);

            MetalHistoryEntryAll metalData = new MetalHistoryEntryAll(unixTimestamp, metalPriceData);
            metalHistoryData.add(metalData);
        }

        return metalHistoryData;
    }

    /**
     * Returns a MetalCurrentData object from the given JSON string.
     *
     * @param json the JSON string containing the current metal data
     * @return a MetalCurrentData object
     */
    public static MetalCurrentData parseMetalCurrentData(String json) {
        Gson gson = GsonSingleton.getGsonInstance();
        return gson.fromJson(json, MetalCurrentData.class);
    }

    /**
     * Returns a map of exchange rates for given currencies from the given JSON string.
     *
     * @param json              the JSON string containing the exchange rate data
     * @param desiredCurrencies the list of desired currency codes
     * @return a map of exchange rates for the given currencies
     */
    public static Map<String, Double> parseExchangeRateData(String json, List<String> desiredCurrencies) {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        JsonObject currenciesObject = jsonObject.getAsJsonObject("currencies");

        Map<String, Double> exchangeRates = new HashMap<>();

        // Find exchange rates for the desired currencies and add them to the map
        for (String currencyCode : desiredCurrencies) {
            if (currenciesObject.has(currencyCode)) {
                double exchangeRate = currenciesObject.get(currencyCode).getAsDouble();
                exchangeRates.put(currencyCode, exchangeRate);
            }
        }
        return exchangeRates;
    }

    /**
     * Returns a map of coin names and their corresponding IDs from the given JSON string.
     * 
     * @param json the JSON string containing a list of coin id-name data.
     * @return a map of coin names and their corresponding IDs.
     */
    public static Map<String, String> parseCoinsList(String json) {
        Gson gson = GsonSingleton.getGsonInstance();

        // Parse the JSON string into a list of Coin objects
        Type listType = new TypeToken<List<Coin>>() {}.getType();
        List<Coin> coins = gson.fromJson(json, listType);

        Map<String, String> coinsMap = new HashMap<>();
        for (Coin coin : coins) {
            coinsMap.put(coin.getName(), coin.getId());
        }

        return coinsMap;
    }
}
