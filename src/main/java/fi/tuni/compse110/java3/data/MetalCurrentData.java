package fi.tuni.compse110.java3.data;

/**
 * Represents the current data for metals, as provided by the API response.
 */
public class MetalCurrentData {

    private Metals metals; // Must be called metals like in the API response

    /**
     * Returns the current metals data.
     *
     * @return the current metals data
     */
    public Metals getCurrentMetals() {
        return metals;
    }
}