package fi.tuni.compse110.java3.utility;

import org.junit.jupiter.api.Test;
import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.*;

public class GsonSingletonTest {

    @Test
    void testSingletonInstanceNotNull() {
        Gson gson = GsonSingleton.getGsonInstance();
        assertNotNull(gson, "Gson instance should not be null");
    }

    @Test
    void testSingletonReturnsSameInstance() {
        Gson gson1 = GsonSingleton.getGsonInstance();
        Gson gson2 = GsonSingleton.getGsonInstance();
        assertSame(gson1, gson2, "Gson instance should be the same");
    }
}