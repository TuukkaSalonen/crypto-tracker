package fi.tuni.compse110.java3.utility;

import fi.tuni.compse110.java3.io.DiskIO;
import fi.tuni.compse110.java3.utility.testobj.TestGsonSerializable;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class DiskIOTest {

    private static Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = DiskIO.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    @Test
    void testCreate() throws Exception {
        File testNoDir = (File) getMethod("create", String.class).invoke(null, "target/test.txt");
        assertNotNull(testNoDir);

        // noinspection ResultOfMethodCallIgnored
        testNoDir.createNewFile();
        assertTrue(testNoDir.exists());

        File testWithDir = (File) getMethod("create", String.class).invoke(null, "target/test/test.txt");
        assertNotNull(testWithDir);
        assertTrue(new File("target/test").exists()); // create promises to create parent directories

        // noinspection ResultOfMethodCallIgnored
        testWithDir.createNewFile();
        assertTrue(testWithDir.exists());
    }

    @Test
    void testReadFromDisk() throws Exception {
        String test = (String) getMethod("readFromDisk", String.class).invoke(null,
                "src/test/resources/fi/tuni/compse110/java3/utility/test.txt");
        assertEquals(test, "This is a test.");
    }

    @Test
    void testReadWriteObject() throws Exception {
        TestGsonSerializable write = new TestGsonSerializable();
        write.val1 = 123;
        write.val2 = 123L;
        write.val3 = 12.3f;
        write.val4 = 12.3d;
        write.val5 = true;
        write.val6 = "test";

        DiskIO.write("target/test.json", write);

        TestGsonSerializable read = (TestGsonSerializable) DiskIO.read("target/test.json", TestGsonSerializable.class);

        assertNotNull(read);
        assertEquals(123, read.val1);
        assertEquals(123L, read.val2);
        assertEquals(12.3f, read.val3);
        assertEquals(12.3d, read.val4);
        assertTrue(read.val5);
        assertEquals("test", read.val6);
    }
}
