package fi.tuni.compse110.java3.utility;

import static org.junit.jupiter.api.Assertions.*;

import fi.tuni.compse110.java3.data.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ParserTest {

    private static String readFile(String filename) throws IOException {
        return Files.readString(Paths.get("src/test/resources/fi/tuni/compse110/java3/utility/" + filename));
    }

    @Test
    void testParseCryptoHistoryData() throws IOException {
        String cryptoHistoryJson = readFile("cryptoHistoryData.json");

        List<CryptoHistoryEntry> history = Parser.parseCryptoHistoryData(cryptoHistoryJson);

        assertEquals(7, history.size());

        CryptoHistoryEntry firstEntry = history.get(0);
        assertEquals(1630454400000L, firstEntry.getTimestamp());
        assertEquals(47000.0, firstEntry.getPrice());
        assertEquals(3500000.0, firstEntry.getVolume());

        CryptoHistoryEntry lastEntry = history.get(6);
        assertEquals(1630972800000L, lastEntry.getTimestamp());
        assertEquals(49500.0, lastEntry.getPrice());
        assertEquals(3800000.0, lastEntry.getVolume());
    }

    @Test
    void testParseMetalHistoryData() throws IOException {
        String metalHistoryJson = readFile("metalHistoryData.json");

        List<MetalHistoryEntryAll> metalHistory = Parser.parseMetalHistoryData(metalHistoryJson);

        assertEquals(4, metalHistory.size());

        MetalHistoryEntryAll firstEntry = metalHistory.get(0);
        assertEquals(1725148800000L, firstEntry.getTimestamp());
        Metals firstMetals = firstEntry.getMetalData();
        assertEquals(1800.0, firstMetals.getGold());
        assertEquals(2300.0, firstMetals.getPalladium());
        assertEquals(1000.0, firstMetals.getPlatinum());
        assertEquals(24.0, firstMetals.getSilver());

        MetalHistoryEntryAll lastEntry = metalHistory.get(3);
        assertEquals(1725408000000L, lastEntry.getTimestamp());
        Metals lastMetals = lastEntry.getMetalData();
        assertEquals(1830.0, lastMetals.getGold());
        assertEquals(2340.0, lastMetals.getPalladium());
        assertEquals(1030.0, lastMetals.getPlatinum());
        assertEquals(25.5, lastMetals.getSilver());
    }

    @Test
    void testParseMetalCurrentData() throws IOException {
        String metalCurrentJson = readFile("metalCurrentData.json");

        MetalCurrentData currentData = Parser.parseMetalCurrentData(metalCurrentJson);

        assertNotNull(currentData);
        Metals metals = currentData.getCurrentMetals();
        assertEquals(1925.0, metals.getGold());
        assertEquals(2450.0, metals.getPalladium());
        assertEquals(1150.0, metals.getPlatinum());
        assertEquals(27.0, metals.getSilver());
    }

    @Test
    void testParseExchangeRateData() throws IOException {
        String exchangeRatesJson = readFile("exchangeRateData.json");
        List<String> desiredCurrencies = Arrays.asList("USD", "EUR", "GBP");

        Map<String, Double> exchangeRates = Parser.parseExchangeRateData(exchangeRatesJson, desiredCurrencies);

        assertEquals(3, exchangeRates.size());
        assertEquals(1.0, exchangeRates.get("USD"));
        assertEquals(0.85, exchangeRates.get("EUR"));
        assertEquals(0.75, exchangeRates.get("GBP"));
    }

    @Test
    void testParseCoinsList() throws IOException {
        String coinsListJson = readFile("coinsListData.json");

        Map<String, String> coinsMap = Parser.parseCoinsList(coinsListJson);

        assertEquals("0chain", coinsMap.get("Zus"));
        assertEquals("01coin", coinsMap.get("01coin"));
    }
}
