package fi.tuni.compse110.java3.data;

/**
 * Represents a historical entry for a cryptocurrency, including timestamp, price, and volume.
 */
public class CryptoHistoryEntry {

    private final long timestamp;
    private final double price;
    private final double volume;

    /**
     * Constructs a new CryptoHistoryEntry with the specified timestamp, price, and volume.
     *
     * @param timestamp the timestamp of the entry
     * @param price     the price of the cryptocurrency at the given timestamp
     * @param volume    the volume of the cryptocurrency traded at the given timestamp
     */
    public CryptoHistoryEntry(long timestamp, double price, double volume) {
        this.timestamp = timestamp;
        this.price = price;
        this.volume = volume;
    }

    /**
     * Returns the timestamp of the entry.
     *
     * @return the timestamp of the entry in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the price of the cryptocurrency at the given timestamp.
     *
     * @return the price of the cryptocurrency
     */
    public double getPrice() {
        return price;
    }

    /**
     * Returns the volume of the cryptocurrency traded at the given timestamp.
     *
     * @return the volume of the cryptocurrency
     */
    public double getVolume() {
        return volume;
    }
}
