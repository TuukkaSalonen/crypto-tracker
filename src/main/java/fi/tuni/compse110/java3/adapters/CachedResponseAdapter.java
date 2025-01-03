package fi.tuni.compse110.java3.adapters;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import fi.tuni.compse110.java3.io.CachedResponse;

/**
 * Gson type adapter for CachedResponse.
 */
public class CachedResponseAdapter extends TypeAdapter<CachedResponse> {

    /**
     * Writes a CachedResponse object to JSON format.
     *
     * @param out      the JsonWriter to write the JSON data to
     * @param response the CachedResponse object to be written to JSON
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void write(JsonWriter out, CachedResponse response) throws IOException {
        out.beginObject();
        out.name("time").value(String.valueOf(response.getTime()));
        out.name("response").value(response.getResponse());
        out.endObject();
    }

    /**
     * Reads a CachedResponse object from JSON format.
     *
     * @param in the JsonReader to read the JSON data from
     * @return the CachedResponse object read from JSON
     * @throws IOException if an I/O error occurs
     */
    @Override
    public CachedResponse read(JsonReader in) throws IOException {
        long time = 0;
        String response = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "time":
                    try {
                        time = Long.parseLong(in.nextString());
                    } catch (NumberFormatException ignored) {
                        // We'll check for valid time in CachedResponse anyway
                    }
                    break;
                case "response":
                    response = in.nextString();
                    break;
            }
        }
        in.endObject();

        return new CachedResponse(time, response);
    }
}