package fi.tuni.compse110.java3.adapters;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import fi.tuni.compse110.java3.preference.Preferences;
import fi.tuni.compse110.java3.preference.PreferencesWrapper;

/**
 * Adapter class for converting PreferencesWrapper objects to JSON and vice versa.
 */
public class PreferencesWrapperAdapter extends TypeAdapter<PreferencesWrapper> {

    /**
     * Writes a PreferencesWrapper object to JSON format.
     *
     * @param out   the JsonWriter to write the JSON data to
     * @param value the PreferencesWrapper object to be written to JSON
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(JsonWriter out, PreferencesWrapper value) throws IOException {
        out.beginObject();
        out.name("preferencesList");
        out.beginArray();
        for (Preferences preferences : value.getPreferencesList()) {
            out.beginObject();
            out.name("name").value(preferences.getName());
            out.name("selectedComparisonType").value(preferences.getSelectedComparisonType());
            out.name("startDate").value(preferences.getStartDate().toString());
            out.name("endDate").value(preferences.getEndDate().toString());
            out.name("selectedCryptocurrencies").beginArray();
            for (String crypto : preferences.getSelectedCryptocurrencies()) {
                out.value(crypto);
            }
            out.endArray();
            out.name("selectedMetals").beginArray();
            for (String metal : preferences.getSelectedMetals()) {
                out.value(metal);
            }
            out.endArray();
            out.name("selectedCurrency").value(preferences.getSelectedCurrency());
            out.endObject();
        }
        out.endArray();
        out.endObject();
    }

     /**
     * Reads a PreferencesWrapper object from JSON format.
     *
     * @param in the JsonReader to read the JSON data from
     * @return the PreferencesWrapper object read from JSON
     * @throws IOException if an I/O error occurs
     */
    @Override
    public PreferencesWrapper read(JsonReader in) throws IOException {
        List<Preferences> preferencesList = new ArrayList<>();
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("preferencesList")) {
                in.beginArray();
                while (in.hasNext()) {
                    in.beginObject();
                    Preferences.PreferencesBuilder preferences = Preferences.builder();
                    while (in.hasNext()) {
                        String fieldName = in.nextName();
                        switch (fieldName) {
                            case "name" -> preferences.name(in.nextString());
                            case "selectedComparisonType" -> preferences.selectedComparisonType(in.nextString());
                            case "startDate" -> preferences.startDate(LocalDate.parse(in.nextString()));
                            case "endDate" -> preferences.endDate(LocalDate.parse(in.nextString()));
                            case "selectedCryptocurrencies" -> {
                                List<String> cryptos = new ArrayList<>();
                                in.beginArray();
                                while (in.hasNext()) {
                                    cryptos.add(in.nextString());
                                }
                                in.endArray();
                                preferences.selectedCryptocurrencies(cryptos);
                            }
                            case "selectedMetals" -> {
                                List<String> metals = new ArrayList<>();
                                in.beginArray();
                                while (in.hasNext()) {
                                    metals.add(in.nextString());
                                }
                                in.endArray();
                                preferences.selectedMetals(metals);
                            }
                            case "selectedCurrency" -> preferences.selectedCurrency(in.nextString());
                        }
                    }
                    in.endObject();
                    preferencesList.add(preferences.build());
                }
                in.endArray();
            }
        }
        in.endObject();
        return new PreferencesWrapper(preferencesList);
    }
}
