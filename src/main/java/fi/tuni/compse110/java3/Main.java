package fi.tuni.compse110.java3;

import java.io.IOException;

import atlantafx.base.theme.NordDark;
import fi.tuni.compse110.java3.utility.DialogUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Main class for launching the application.
 */
public class Main extends Application {

    /**
     * The main entry point for the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     *
     * @param stage the primary stage for this application
     * @throws IOException if an I/O error occurs during loading the FXML file
     */
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));

        Pane rootPane = fxmlLoader.load();
        Scene scene = new Scene(rootPane, 1000, 1000);

        stage.setMinWidth(700);
        stage.setMinHeight(700);

        stage.setScene(scene);
        stage.setTitle("Treasure Tracker");
        DialogUtils.setStageIcon(stage);
        stage.show();
    }
}
