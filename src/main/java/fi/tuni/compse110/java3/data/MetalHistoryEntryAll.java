package fi.tuni.compse110.java3.data;

/**
 * Represents a historical entry for metals, including timestamp and metal data.
 */
public class MetalHistoryEntryAll {

    private final long timestamp;
    private final Metals metalData;

    /**
     * Constructs a new MetalHistoryEntryAll with the specified timestamp and metal data.
     *
     * @param timestamp the timestamp of the entry
     * @param metalData the metal data at the given timestamp
     */
    public MetalHistoryEntryAll(long timestamp, Metals metalData) {
        this.timestamp = timestamp;
        this.metalData = metalData;
    }

    /**
     * Returns the timestamp of the entry.
     *
     * @return the timestamp of the entry
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the metal data at the given timestamp.
     *
     * @return the metal data
     */
    public Metals getMetalData() {
        return metalData;
    }
}