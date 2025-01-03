package fi.tuni.compse110.java3.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

public class HistoryChunkTest {

    @Test
    void testGetChunkIndex() {
        LocalDate date = LocalDate.of(2024, 10, 1);
        int size = 30;
        int expectedIndex = (int) (ChronoUnit.DAYS.between(LocalDate.of(2000, 1, 1), date) / size);
        assertEquals(expectedIndex, HistoryChunk.getChunkIndex(date, size));

        // Additional cases
        assertEquals(0, HistoryChunk.getChunkIndex(LocalDate.of(2000, 1, 1), size));
        assertEquals(1, HistoryChunk.getChunkIndex(LocalDate.of(2000, 1, 31), size));
        assertEquals(12, HistoryChunk.getChunkIndex(LocalDate.of(2001, 1, 15), size));
    }

    @Test
    void testGetChunkStart() {
        long chunk = 100;
        int size = 30;
        LocalDate expectedStart = LocalDate.of(2000, 1, 1).plusDays(chunk * size);
        assertEquals(expectedStart, HistoryChunk.getChunkStart(chunk, size));

        // Additional cases
        assertEquals(LocalDate.of(2000, 1, 1), HistoryChunk.getChunkStart(0, size));
        assertEquals(LocalDate.of(2000, 1, 31), HistoryChunk.getChunkStart(1, size));
        assertEquals(LocalDate.of(2001, 1, 25), HistoryChunk.getChunkStart(13, size));
    }

    @Test
    void testGetChunkEnd() {
        long chunk = 100;
        int size = 30;
        LocalDate expectedEnd = LocalDate.of(2000, 1, 1).plusDays((chunk + 1) * size - 1);
        assertEquals(expectedEnd, HistoryChunk.getChunkEnd(chunk, size));

        // Additional cases
        assertEquals(LocalDate.of(2000, 1, 30), HistoryChunk.getChunkEnd(0, size));
        assertEquals(LocalDate.of(2000, 2, 29), HistoryChunk.getChunkEnd(1, size));
        assertEquals(LocalDate.of(2001, 1, 24), HistoryChunk.getChunkEnd(12, size));
    }
}
