package fi.tuni.compse110.java3.preference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for user preference choices.
 */
public class Preferences {

    private String name;
    private String selectedComparisonType;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> selectedCryptocurrencies;
    private List<String> selectedMetals;
    private String selectedCurrency;

    @SuppressWarnings("unused")
    private Preferences() {
        // For GsonSerializable, accessible only through reflection
    }

    /**
     * Constructor for preferences.
     * 
     * @param name                     preference name
     * @param selectedComparisonType   type of comparison
     * @param startDate                start date
     * @param endDate                  end date
     * @param selectedCryptocurrencies selected cryptocurrencies
     * @param selectedMetals           selected metals
     * @param selectedCurrency         selected currency
     */
    private Preferences(String name, String selectedComparisonType, LocalDate startDate, LocalDate endDate,
            List<String> selectedCryptocurrencies, List<String> selectedMetals, String selectedCurrency) {
        this.name = name;
        this.selectedComparisonType = selectedComparisonType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.selectedCryptocurrencies = selectedCryptocurrencies;
        this.selectedMetals = selectedMetals;
        this.selectedCurrency = selectedCurrency;
    }

    /**
     * Get name.
     * 
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set preference name.
     * 
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get selected comparison type.
     * 
     * @return selected comparison type
     */
    public String getSelectedComparisonType() {
        return selectedComparisonType;
    }

    /**
     * Get start date.
     * 
     * @return start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Get end date.
     * 
     * @return end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Get selected cryptocurrencies.
     * 
     * @return selected cryptocurrencies
     */
    public List<String> getSelectedCryptocurrencies() {
        return selectedCryptocurrencies;
    }

    /**
     * Get selected metals.
     * 
     * @return selected metals
     */
    public List<String> getSelectedMetals() {
        return selectedMetals;
    }

    /**
     * Get selected currency.
     * 
     * @return selected currency
     */
    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    /**
     * Returns a new PreferencesBuilder instance.
     *
     * @return a new PreferencesBuilder instance
     */
    public static PreferencesBuilder builder() {
        return new PreferencesBuilder();
    }

    /**
     * Builder class for Preferences.
     */
    public static class PreferencesBuilder {

        private String name;
        private String selectedComparisonType;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> selectedCryptocurrencies;
        private List<String> selectedMetals;
        private String selectedCurrency;

        /**
         * Builds and returns a Preferences object.
         *
         * @return a new Preferences object
         * @throws RuntimeException if required fields are not set
         */
        public Preferences build() {
            if (name == null) {
                throw new RuntimeException("name must not be null");
            }
            if (selectedComparisonType == null) {
                selectedComparisonType = "Price";
            }
            if (startDate == null) {
                startDate = LocalDate.now();
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }
            if (selectedCurrency == null) {
                selectedCurrency = "USD";
            }
            if (selectedCryptocurrencies == null) {
                selectedCryptocurrencies = new ArrayList<>();
            }
            if (selectedMetals == null) {
                selectedMetals = new ArrayList<>();
            }
            return new Preferences(name, selectedComparisonType, startDate, endDate,
                    selectedCryptocurrencies, selectedMetals, selectedCurrency);
        }

        /**
         * Sets the name for the Preferences object.
         *
         * @param name the name to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the selected comparison type for the Preferences object.
         *
         * @param selectedComparisonType the selected comparison type to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder selectedComparisonType(String selectedComparisonType) {
            this.selectedComparisonType = selectedComparisonType;
            return this;
        }

        /**
         * Sets the start date for the Preferences object.
         *
         * @param startDate the start date to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        /**
         * Sets the end date for the Preferences object.
         *
         * @param endDate the end date to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        /**
         * Sets the selected cryptocurrencies for the Preferences object.
         *
         * @param selectedCryptocurrencies the selected cryptocurrencies to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder selectedCryptocurrencies(List<String> selectedCryptocurrencies) {
            this.selectedCryptocurrencies = selectedCryptocurrencies;
            return this;
        }

        /**
         * Sets the selected metals for the Preferences object.
         *
         * @param selectedMetals the selected metals to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder selectedMetals(List<String> selectedMetals) {
            this.selectedMetals = selectedMetals;
            return this;
        }

        /**
         * Sets the selected currency for the Preferences object.
         *
         * @param selectedCurrency the selected currency to set
         * @return the current PreferencesBuilder instance
         */
        public PreferencesBuilder selectedCurrency(String selectedCurrency) {
            this.selectedCurrency = selectedCurrency;
            return this;
        }
    }
}
