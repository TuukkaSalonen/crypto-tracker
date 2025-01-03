package fi.tuni.compse110.java3.preference;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fi.tuni.compse110.java3.Controller;
import fi.tuni.compse110.java3.io.DiskIO;
import fi.tuni.compse110.java3.utility.Constants;
import fi.tuni.compse110.java3.utility.DialogUtils;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Class for saving and loading user preferences.
 */
public class UserPreferenceManager {

    private PreferencesWrapper wrapper;

    private final Controller controller;

    /**
     * Constructor for UserPreferenceManager.
     * 
     * @param controller the controller instance
     */
    public UserPreferenceManager(Controller controller) {
        this.controller = controller;
    }

    /**
     * Get used PreferencesWrapper instance or load/create it if not loaded yet.
     */
    private PreferencesWrapper getWrapper() {
        if (wrapper == null) {
            try {
                wrapper = (PreferencesWrapper) DiskIO.read(
                        Constants.USER_PREFERENCES_FILENAME, PreferencesWrapper.class);

                if (wrapper == null) {
                    wrapper = new PreferencesWrapper(new ArrayList<>());
                }
            } catch (Exception ex) {
                DialogUtils.showErrorDialog("Failed to load preferences", ex);

                wrapper = new PreferencesWrapper(new ArrayList<>());
            }
        }

        return wrapper;
    }

    /**
     * Save user preferences to a file.
     * 
     * @param name       preference name
     * @param preference preference to save
     * @throws IllegalArgumentException if no cryptocurrencies/metals are selected
     * @throws RuntimeException         if an error with the file writing occurs
     */
    private void saveUserPreferences(String name, Preferences preference) {
        List<Preferences> preferencesList = getWrapper().getPreferencesList();

        try {
            if (checkIfPreferenceExists(name, preferencesList)) {
                throw new IllegalArgumentException("Preference with the same name already exists");
            }
            if (preferencesList.size() >= 10) {
                throw new IllegalArgumentException("Maximum number of preferences reached");
            }
            // Add the new preference to the list
            preferencesList.add(preference);

            // Write the updated list back to the file
            DiskIO.write(Constants.USER_PREFERENCES_FILENAME, getWrapper());
            controller.displayUserPreferences(preferencesList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update user preference fully from file.
     * 
     * @param name       new name
     * @param preference preference to update
     * @throws IOException if an error with the file reading occurs
     */
    private void replaceUserPreference(String name, Preferences preference) throws IOException {
        List<Preferences> preferencesList = getWrapper().getPreferencesList();

        try {
            for (int i = 0; i < preferencesList.size(); i++) {
                if (preferencesList.get(i).getName().equals(name)) {
                    preferencesList.set(i, preference);
                    break;
                }
            }
            DiskIO.write(Constants.USER_PREFERENCES_FILENAME, getWrapper());
            controller.displayUserPreferences(preferencesList);
        } catch (IOException e) {
            throw new IOException("Error updating user preferences", e);
        }
    }

    /**
     * Update user preference name from file.
     *
     * @param name       new name
     * @param preference preference to update
     * @throws IOException if an error with the file reading occurs
     */
    private void updateUserPreferenceName(Preferences preference, String name) throws IOException {
        List<Preferences> preferencesList = getWrapper().getPreferencesList();

        try {
            for (int i = 0; i < preferencesList.size(); i++) {
                if (preferencesList.get(i).getName().equals(preference.getName())) {
                    preference.setName(name);
                    preferencesList.set(i, preference);
                    break;
                }
            }
            DiskIO.write(Constants.USER_PREFERENCES_FILENAME, getWrapper());
            controller.displayUserPreferences(preferencesList);
        } catch (IOException e) {
            throw new IOException("Error updating user preference", e);
        }
    }

    /**
     * Delete user preference from file.
     * 
     * @param preference preference to delete
     * @throws IOException if an error with the file reading occurs
     */
    private void deleteUserPreferences(Preferences preference) throws IOException {
        List<Preferences> preferencesList = getWrapper().getPreferencesList();

        try {
            preferencesList.removeIf(p -> p.getName().equals(preference.getName()));
            DiskIO.write(Constants.USER_PREFERENCES_FILENAME, getWrapper());
            controller.displayUserPreferences(preferencesList);
        } catch (IOException e) {
            throw new IOException("Error deleting user preferences", e);
        }
    }

    /**
     * Check if preference exists in the list.
     * 
     * @param name            preference name
     * @param preferencesList list of preferences
     * @return true if preference exists, false otherwise
     */
    private static boolean checkIfPreferenceExists(String name, List<Preferences> preferencesList) {
        for (Preferences preference : preferencesList) {
            if (preference.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Show a dialog to edit/delete user preferences.
     */
    public void showEditPreferencesPopup() {
        if (getWrapper().getPreferencesList().isEmpty()) {
            DialogUtils.showWarningDialog("Failed", "No saved preferences found");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Preferences");
        DialogUtils.setDialogIcon(dialog);

        // Create the content for the dialog
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        ListView<Preferences> listView = new ListView<>();
        listView.getItems().addAll(getWrapper().getPreferencesList());

        // Set custom cell factory to display preferences
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Preferences item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        // Set the height of the ListView based on the number of items
        listView.prefHeightProperty().bind(Bindings.size(listView.getItems()).multiply(40));

        grid.add(new Label("Select Preference:"), 0, 0);
        grid.add(listView, 0, 1, 2, 1);

        TextField nameField = new TextField();
        nameField.setDisable(true);

        grid.add(new Label("Edit name:"), 0, 2);
        grid.add(nameField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Create custom ButtonType for delete action
        ButtonType buttonTypeDelete = new ButtonType("Delete", ButtonBar.ButtonData.OTHER);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, buttonTypeDelete);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Button deleteButton = (Button) dialog.getDialogPane().lookupButton(buttonTypeDelete);

        // Handle the OK button action
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            Preferences selectedPreference = listView.getSelectionModel().getSelectedItem();
            if (selectedPreference == null) {
                DialogUtils.showWarningDialog("Failed", "No preference selected");
                event.consume();
            } else {
                try {
                    String newName = nameField.getText();
                    if (newName.trim().isEmpty()) {
                        DialogUtils.showWarningDialog("Invalid Input", "Input cannot be empty.");
                        event.consume();
                    } else {
                        updateUserPreferenceName(selectedPreference, newName);
                    }
                } catch (IOException e) {
                    DialogUtils.showErrorDialog("Failed to update preferences", e);
                }
            }
        });

        // Handle the Delete button action
        deleteButton.addEventFilter(ActionEvent.ACTION, event -> {
            Preferences selectedPreference = listView.getSelectionModel().getSelectedItem();
            if (selectedPreference == null) {
                DialogUtils.showWarningDialog("Failed", "No preference selected");
                event.consume();
            } else {
                try {
                    deleteUserPreferences(selectedPreference);
                } catch (IOException e) {
                    DialogUtils.showErrorDialog("Failed to delete preferences", e);
                }
            }
        });
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                nameField.setText(newValue.getName());
                nameField.setDisable(false);
            }
        });
        dialog.showAndWait();
    }

    /**
     * Save current choices as user preferences.
     *
     * @param selectedCurrencies     the list of selected cryptocurrencies
     * @param selectedMetals         the list of selected metals
     * @param startDate              the start date of the selected date range
     * @param endDate                the end date of the selected date range
     * @param selectedCurrency       the selected currency for price comparison
     * @param selectedComparisonType the selected comparison type
     */
    public void saveUserPreferences(List<String> selectedCurrencies, List<String> selectedMetals, LocalDate startDate,
            LocalDate endDate, String selectedCurrency, String selectedComparisonType) {

        DialogUtils.showInputDialog("Save preferences", "Enter a name for your preference", "Name:")
                .ifPresent(name -> {

                    try {
                        Optional<Preferences> existingPreference = getWrapper().getPreferencesList().stream()
                                .filter(p -> p.getName().equals(name))
                                .findFirst();

                        // Build the preferences object with the user's choices
                        Preferences preference = Preferences.builder()
                                .name(name)
                                .selectedComparisonType(selectedComparisonType)
                                .startDate(startDate)
                                .endDate(endDate)
                                .selectedCryptocurrencies(selectedCurrencies)
                                .selectedMetals(selectedMetals)
                                .selectedCurrency(selectedCurrency)
                                .build();

                        if (existingPreference.isPresent()) {
                            // Prompt the user for confirmation to replace the existing preference
                            Optional<ButtonType> confirmation = DialogUtils.showConfirmationDialog(
                                    "Replace Preference",
                                    "A preference with name " + name + " already exists.",
                                    "Do you want to replace it?");

                            if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                                replaceUserPreference(name, preference);
                            }
                        } else {
                            saveUserPreferences(name, preference);
                        }
                    } catch (IOException | RuntimeException e) {
                        DialogUtils.showErrorDialog("Failed to save preferences", e);
                    }
                });
    }

    /**
     * Load all preferences from the file and add them to the preferencesList.
     */
    public void loadPreferences() {
        controller.displayUserPreferences(getWrapper().getPreferencesList());
    }
}
