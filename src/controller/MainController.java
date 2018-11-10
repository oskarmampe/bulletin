package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController {

    public MainController(Stage stage) {
        mainStage = stage;
    }

    private Stage mainStage;

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    public void setScene(Pane node) {
        mainStage.setScene(new Scene(node));
        //scene.getStylesheets().setAll(getClass().getResource("style.css").toExternalForm());
    }
}
