package fi.tuni.compse110.java3.io;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Reads and writes data from/to disk.
 */
public class DiskIO {

    /**
     * Creates given file and its parent directories.
     * 
     * @param fileName target file
     * @return the created file object
     */
    private static File create(String fileName) {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (parent != null) {
            // noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }
        return file;
    }

    /**
     * Writes given object to disk as a json file serialized by Gson.
     * 
     * @param outputFile target file
     * @param object     the object to write
     * @throws IOException if file couldn't be written
     */
    public static void write(String outputFile, GsonSerializable object) throws IOException {
        File file = create(outputFile);

        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            object.getGson().toJson(object, fileWriter);
        }
    }

    /**
     * Reads given json file as object of given type.
     * 
     * @param inputFile target file
     * @param type      object type
     * @return object parsed by Gson or null if file not found
     * @throws IOException if file couldn't be read
     * @throws Exception   if anything goes wrong in Gson
     */
    public static GsonSerializable read(String inputFile, Class<? extends GsonSerializable> type)
            throws IOException, Exception {

        String contents = readFromDisk(inputFile);
        if (contents == null) {
            return null;
        }

        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            GsonSerializable instance = (GsonSerializable) constructor.newInstance();

            return instance.getGson().fromJson(contents, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads file contents from disk as utf-8 string.
     * 
     * @param inputFile target file
     * @return string or null if file does not exist
     * @throws IOException if file couldn't be read
     */
    private static String readFromDisk(String inputFile) throws IOException {
        File file = new File(inputFile);

        if (!file.exists()) {
            return null;
        }

        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            StringBuilder out = new StringBuilder();

            while (scanner.hasNext()) {
                out.append(scanner.nextLine());
            }

            return out.toString();
        }
    }
}
