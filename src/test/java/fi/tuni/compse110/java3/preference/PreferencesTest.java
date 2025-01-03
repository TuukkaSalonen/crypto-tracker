package fi.tuni.compse110.java3.preference;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesTest {

    @Test
    public void testPreferencesBuilder() {
        String name = "Test Preference";
        String comparisonType = "Price";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        List<String> cryptocurrencies = Arrays.asList("Bitcoin", "Ethereum");
        List<String> metals = Arrays.asList("Gold", "Silver");
        String currency = "USD";

        Preferences preferences = Preferences.builder()
                .name(name)
                .selectedComparisonType(comparisonType)
                .startDate(startDate)
                .endDate(endDate)
                .selectedCryptocurrencies(cryptocurrencies)
                .selectedMetals(metals)
                .selectedCurrency(currency)
                .build();

        assertEquals(name, preferences.getName());
        assertEquals(comparisonType, preferences.getSelectedComparisonType());
        assertEquals(startDate, preferences.getStartDate());
        assertEquals(endDate, preferences.getEndDate());
        assertEquals(cryptocurrencies, preferences.getSelectedCryptocurrencies());
        assertEquals(metals, preferences.getSelectedMetals());
        assertEquals(currency, preferences.getSelectedCurrency());
    }

    @Test
    public void testDefaultValues() {
        Preferences preferences = Preferences.builder()
                .name("Default Test")
                .build();

        assertEquals("Default Test", preferences.getName());
        assertEquals("Price", preferences.getSelectedComparisonType());
        assertEquals(LocalDate.now(), preferences.getStartDate());
        assertEquals(LocalDate.now(), preferences.getEndDate());
        assertEquals("USD", preferences.getSelectedCurrency());
        assertTrue(preferences.getSelectedCryptocurrencies().isEmpty());
        assertTrue(preferences.getSelectedMetals().isEmpty());
    }

    @Test
    public void testSetName() {
        Preferences preferences = Preferences.builder()
                .name("Initial Name")
                .build();

        preferences.setName("Updated Name");
        assertEquals("Updated Name", preferences.getName());
    }

    @Test
    public void testMissingNameThrowsException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Preferences.builder().build();
        });
        assertEquals("name must not be null", exception.getMessage());
    }

    @Test
    public void testNullValuesForOptionalFields() {
        Preferences preferences = Preferences.builder()
                .name("Null Test")
                .selectedCryptocurrencies(null)
                .selectedMetals(null)
                .selectedCurrency(null)
                .build();

        assertNotNull(preferences.getSelectedCryptocurrencies());
        assertNotNull(preferences.getSelectedMetals());
        assertEquals("USD", preferences.getSelectedCurrency());
    }

    @Test
    public void testExplicitEmptyLists() {
        Preferences preferences = Preferences.builder()
                .name("Explicit Empty Test")
                .selectedCryptocurrencies(new ArrayList<>())
                .selectedMetals(new ArrayList<>())
                .build();

        assertTrue(preferences.getSelectedCryptocurrencies().isEmpty());
        assertTrue(preferences.getSelectedMetals().isEmpty());
    }
}