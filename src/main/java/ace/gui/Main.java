package ace.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Entry point for the JavaFX GUI. This starts out as a bare-bones "Hello
 * World" window, matching the first step of the JavaFX tutorial, purely to
 * confirm JavaFX itself is wired up correctly before connecting it to Ace's
 * actual command logic.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label helloWorld = new Label("Hello World!");
        Scene scene = new Scene(helloWorld);

        stage.setScene(scene);
        stage.show();
    }
}
