package fi.tuni.compse110.java3.utility;

import java.util.Optional;

import fi.tuni.compse110.java3.Main;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * Reusable methods to create and show dialogs to user.
 */
public class DialogUtils {

    /**
     * Shows a warning dialog with the given title and header.
     *
     * @param title  the title of the dialog
     * @param header the header text of the dialog
     */
    public static void showWarningDialog(String title, String header) {
        showAlertDialog(title, header, "", Alert.AlertType.WARNING);
    }

    /**
     * Shows an error dialog with the given title, header and error message.
     * Also prints stack trace to console.
     *
     * @param header    the header text of the dialog
     * @param exception the exception whose message will be shown in the dialog
     */
    public static void showErrorDialog(String header, Exception exception) {
        // noinspection CallToPrintStackTrace
        exception.printStackTrace();
        showAlertDialog("Error", header, exception.getMessage(), Alert.AlertType.ERROR);
    }

    /**
     * Shows dialog of given type, title and header.
     *
     * @param title   the title of the dialog
     * @param header  the header text of the dialog
     * @param content the content text of the dialog
     * @param type    the type of the alert dialog
     */
    private static void showAlertDialog(String title, String header, String content, Alert.AlertType type) {
        Alert dialog = new Alert(type);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        setDialogIcon(dialog);
        dialog.showAndWait();
    }

    /**
     * Shows an input dialog with the given title, header, and content.
     * Returns optional user input text.
     *
     * @param title   the title of the dialog
     * @param header  the header text of the dialog
     * @param content the content text of the dialog
     * @return an optional containing the user input text, or an empty optional if
     *         the dialog was canceled
     */
    public static Optional<String> showInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        setDialogIcon(dialog);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String input = dialog.getEditor().getText();
            if (input.trim().isEmpty()) {
                event.consume(); // Prevent the dialog from closing
                DialogUtils.showWarningDialog("Invalid Input", "Input cannot be empty.");
            }
        });
        return dialog.showAndWait();
    }

    /**
     * Shows a confirmation dialog with the given title, header, and content.
     * Returns optional user choice.
     *
     * @param title   the title of the dialog
     * @param header  the header text of the dialog
     * @param content the content text of the dialog
     * @return an optional containing the user's choice, or an empty optional if the
     *         dialog was canceled
     */
    public static Optional<ButtonType> showConfirmationDialog(String title, String header, String content) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        setDialogIcon(dialog);
        return dialog.showAndWait();
    }

    /**
     * Shows an informational dialog with the given title, header, and content.
     *
     * @param title   the title of the dialog
     * @param header  the header text of the dialog
     * @param content the content text of the dialog
     */
    private static void showInfoDialog(String title, String header, String content) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        setDialogIcon(dialog);

        TextArea textArea = new TextArea();
        textArea.setText(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        // Set the TextArea to grow with the dialog
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane contentPane = new GridPane();
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.add(textArea, 0, 0);

        // Set the content of the dialog
        dialog.getDialogPane().setContent(contentPane);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText("Got it!");

        dialog.showAndWait();
    }

    /**
     * Shows a help dialog with instructions on how to use the app.
     */
    public static void showHelpDialog() {
        String helpContent = """
                Welcome to the Treasure Tracker App!

                Here are the general steps of using the app:

                1. Choose the comparison type from the dropdown menu (Price, %-Change or Volume). Note that volume is only available for cryptocurrencies. The %-Change option shows the price change percentage for each data point compared to the first data point in the selected time period.

                2. Select the time period for the data. The range is limited to 1 year.

                3. Check the checkboxes for cryptocurrencies or metals. This unlocks the comboboxes where you can select the commodies you want to track. Cryptocurrencies are checked by default.

                4. Select the currency for displaying the prices in. The default is USD. This option is only available when tracking prices.

                5. Click the 'Update' button to refresh the charts with the selected data. The crypto trading volumes pie chart is always displayed when cryptocurrencies are selected.

                6. View the data:
                Use the zoom (scrolling) and pan (dragging when zoomed in) features on the line chart to explore the data in more detail. Hover over the data points to see the exact values.
                The table shows each data point in the chart and you can sort the data by clicking the column headers. You can reorder the columns in the table by dragging the headers to your preferred position. Additionally, if you want to see more data rows, you can drag the bottom of the table to resize it.
                Hover over the pie chart slices or the cryptocurrency name labels below the pie chart to see the total volume values.

                7. Add more cryptocurrencies with the 'Add Cryptos' button. This opens a window where you can search from thousands of cryptocurrencies to add to the list.

                8. The Preferences menu allows you to save, edit and load your preferences for the app.
                Save your preferences using the 'Save Preference' button. This saves the current selection.
                Load saved preferences from the 'Load Preferences' menu.
                Edit your preferences using the 'Edit Preferences' button. You can edit the name or delete the preference.

                9. Reset the application to the default state with the 'Reset To Default' button in the Actions menu. This also removes all added cryptocurrencies and resets the crypto list to the default list.
                """;
        showInfoDialog("Help", "How to Use the Application", helpContent);
    }

    /**
     * Shows an about dialog with information about the application.
     */
    public static void showAboutDialog() {
        String aboutContent = """
                Treasure Tracker App

                Project for course COMP.SE.110-2024-2025-1 Software Design

                Developed by group Java3+

                This application allows users to track the prices and volumes of various cryptocurrencies and metals.

                The data is fetched from CoinGecko API and Metals.dev API.
                """;
        showInfoDialog("About", "About Treasure Tracker", aboutContent);
    }

    /**
     * Sets the application icon for the given dialog window.
     * 
     * @param dialog the dialog to set the icon for
     */
    public static void setDialogIcon(Dialog<?> dialog) {
        setStageIcon((Stage) dialog.getDialogPane().getScene().getWindow());
    }

    /**
     * Sets the application icon for the given stage.
     * 
     * @param stage the stage to set the icon for
     */
    public static void setStageIcon(Stage stage) {
        try {
            stage.getIcons().add(new Image(Main.class.getResourceAsStream("/icon.jpg")));
        } catch (Exception ignore) {}
    }
}
