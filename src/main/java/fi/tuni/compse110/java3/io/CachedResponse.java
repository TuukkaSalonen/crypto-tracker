package fi.tuni.compse110.java3.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.tuni.compse110.java3.adapters.CachedResponseAdapter;

/**
 * Represents a cached API response.
 */
public class CachedResponse extends GsonSerializable {

    private long time;
    private String response;

    /**
     * Default constructor for GsonSerializable.
     */
    @SuppressWarnings("unused")
    private CachedResponse() {
        // For GsonSerializable, accessible only through reflection
    }

    /**
     * Constructs a new CachedResponse with the specified time and response.
     *
     * @param time     the time when the original request was made
     * @param response the response content as received from the API
     */
    public CachedResponse(long time, String response) {
        this.time = time;
        this.response = response;
    }

    /**
     * Returns the time when the original request was made.
     *
     * @return the time when the original request was made
     */
    public long getTime() {
        return time;
    }

    /**
     * Returns the response content as received from the API.
     *
     * @return the response content
     */
    public String getResponse() {
        return response;
    }

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(CachedResponse.class, new CachedResponseAdapter()).create();

    /**
     * Returns the Gson instance configured for this class.
     *
     * @return the Gson instance
     */
    @Override
    public Gson getGson() {
        return GSON;
    }
}
