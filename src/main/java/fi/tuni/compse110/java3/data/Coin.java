package fi.tuni.compse110.java3.data;

/**
 * Represents a single coin from the coingecko Coins List (ID Map) API endpoint.
 */
public class Coin {
    private String name;
    private String id;

    /**
     * Returns the name of the coin.
     * 
     * @return name of the coin.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id of the coin.
     * 
     * @return id of the coin.
     */
    public String getId() {
        return id;
    }
}
