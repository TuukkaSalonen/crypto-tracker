package fi.tuni.compse110.java3.utility;

import fi.tuni.compse110.java3.io.CachedRequest;
import fi.tuni.compse110.java3.io.CachedResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CachedRequestTest {

    @Test
    void testReadCached() throws Exception {
        Method method = CachedRequest.class.getDeclaredMethod("readCached", String.class, long.class, long.class);
        method.setAccessible(true);

        CachedResponse test = (CachedResponse) method.invoke(null,
                "src/test/resources/fi/tuni/compse110/java3/utility/794087530", 0, 0);

        assertEquals(1728478389141L, test.getTime());
        assertEquals("{\"prices\":[],\"market_caps\":[],\"total_volumes\":[]}", test.getResponse());
    }
}
