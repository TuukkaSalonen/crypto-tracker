package fi.tuni.compse110.java3.utility;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Provides utils to group days into chunks of given size.
 */
public class HistoryChunk {

    /**
     * Used to calculate chunk index.
     */
    private static final LocalDate ZERO_DATE = LocalDate.of(2000, 1, 1);

    /**
     * Splits dates into chunks of the specified size starting from ZERO_DATE. Used as history cache index.
     *
     * @param date the date to calculate the chunk index for
     * @param size the size of each chunk in days
     * @return the chunk index for the given date
     */
    public static int getChunkIndex(LocalDate date, int size) {
        return (int) (ChronoUnit.DAYS.between(ZERO_DATE, date) / size);
    }

    /**
     * Gets the start LocalDate of a history chunk.
     *
     * @param chunk the chunk index
     * @param size  the size of each chunk in days
     * @return the start LocalDate of the specified chunk
     */
    public static LocalDate getChunkStart(long chunk, int size) {
        return ZERO_DATE.plusDays(chunk * size);
    }

    /**
     * Gets the end LocalDate of a history chunk.
     *
     * @param chunk the chunk index
     * @param size  the size of each chunk in days
     * @return the end LocalDate of the specified chunk
     */
    public static LocalDate getChunkEnd(long chunk, int size) {
        return ZERO_DATE.plusDays((chunk + 1) * size - 1);
    }
}