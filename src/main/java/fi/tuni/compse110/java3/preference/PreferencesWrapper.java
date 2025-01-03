package fi.tuni.compse110.java3.preference;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.tuni.compse110.java3.adapters.PreferencesWrapperAdapter;
import fi.tuni.compse110.java3.io.GsonSerializable;

/**
 * Wrapper class for listing Preferences.
 */
public class PreferencesWrapper extends GsonSerializable {

    private List<Preferences> preferencesList;

    /**
     * Default constructor for GsonSerializable, accessible only through reflection.
     */
    @SuppressWarnings("unused")
    private PreferencesWrapper() {
        // For GsonSerializable, accessible only through reflection
    }

    /**
     * Constructs a new PreferencesWrapper with the specified list of preferences.
     *
     * @param preferencesList the list of preferences
     */
    public PreferencesWrapper(List<Preferences> preferencesList) {
        this.preferencesList = preferencesList;
    }

    /**
     * Returns the list of preferences.
     *
     * @return the list of preferences
     */
    public List<Preferences> getPreferencesList() {
        return preferencesList;
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(PreferencesWrapper.class, new PreferencesWrapperAdapter()).create();

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