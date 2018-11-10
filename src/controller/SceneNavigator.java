package controller;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class SceneNavigator {

    public static final String MAIN    = "../view/welcome.fxml";
    public static final String SEND = "../view/send.fxml";
    public static final String TOPICS = "../view/topics.fxml";

    private static MainController mainController;

    public static void setMainController(MainController mainController) {
        SceneNavigator.mainController = mainController;
    }


    public static void loadScene(String fxml) {
        try {
            mainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
