package controller;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;

public class SceneNavigator {

    public static final String MAIN = "../view/main.fxml";
    public static final String WELCOME = "../view/welcome.fxml";
    public static final String CREATE_TOPIC = "../view/create_topic.fxml";
    public static final String READ_ALL_TOPICS = "../view/read_topics.fxml";
    public static final String VIEW_TOPIC = "../view/view_topic.fxml";

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

    public static void loadScene(String fxml, HashMap map) {
        try {
            FXMLLoader loader = new FXMLLoader();
            mainController.setScene(loader.load(SceneNavigator.class.getResource(fxml).openStream()));
            ParametrizedController controller = loader.getController();
            controller.setParamters(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
