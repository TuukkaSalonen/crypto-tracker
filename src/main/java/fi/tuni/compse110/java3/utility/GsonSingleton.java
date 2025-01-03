package fi.tuni.compse110.java3.utility;

import com.google.gson.Gson;

/**
 * Singleton class for providing a single Gson instance.
 */
public class GsonSingleton {

    private static final Gson gsonInstance = new Gson();

    private GsonSingleton() {
    }

    /**
     * Returns the singleton Gson instance.
     *
     * @return the singleton Gson instance
     */
    public static Gson getGsonInstance() {
        return gsonInstance;
    }
}