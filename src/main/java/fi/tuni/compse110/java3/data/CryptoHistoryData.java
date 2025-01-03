package fi.tuni.compse110.java3.data;

import java.util.List;

/**
 * Helper class for parsing JSON data related to cryptocurrency history.
 */
public class CryptoHistoryData {

    private List<List<Number>> prices;
    private List<List<Number>> total_volumes;

    /**
     * Returns the price data for the cryptocurrency.
     *
     * @return a list of lists containing the price data, where each inner list contains a timestamp and a price
     */
    public List<List<Number>> getPriceData() {
        return prices;
    }

    /**
     * Returns the total volume data for the cryptocurrency.
     *
     * @return a list of lists containing the total volume data, where each inner list contains a timestamp and a volume
     */
    public List<List<Number>> getTotalVolumes() {
        return total_volumes;
    }
}
