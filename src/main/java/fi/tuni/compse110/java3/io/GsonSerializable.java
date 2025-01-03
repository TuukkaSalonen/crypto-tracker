package fi.tuni.compse110.java3.io;

import com.google.gson.Gson;

/**
 * Implements Gson serialization.
 */
public abstract class GsonSerializable {

    /**
     * Get Gson with correct type adapter for this object.
     * 
     * @return Gson object.
     */
    public abstract Gson getGson();
}
