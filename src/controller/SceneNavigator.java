package controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class SceneNavigator {

    public static final String MAIN = "../view/main.fxml";
    public static final String WELCOME = "../view/welcome.fxml";
    public static final String READ_ALL_TOPICS = "../view/read_topics.fxml";
    public static final String VIEW_TOPIC = "../view/view_topic.fxml";
    public static final String CREATE_USER = "../view/create_user.fxml";
    public static final String BASIC_POPUP = "../view/popup_basic.fxml";
    public static final String CREATE_TOPIC_POPUP = "../view/popup_create_topic.fxml";
    public static final String EDIT_TOPIC_POPUP = "../view/popup_edit_topic.fxml";

    private static MainController mainController;

    public static Stage mPrimaryStage;
    private static Stage mPopupStage;

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
            controller.setParameters(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showBasicPopupWindow(String labelText) {
        try {
            FXMLLoader loader = new FXMLLoader();

            mPopupStage = new Stage();
            mPopupStage.setResizable(false);
            mPopupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(loader.load(SceneNavigator.class.getResource(BASIC_POPUP).openStream()));
            ((PopupController)loader.getController()).setLabelText(labelText);
            mPopupStage.setScene(popupScene);
            mPopupStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void closePopupWindow() {
        mPopupStage.close();
    }

    public static void showPopupWindow(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader();

            mPopupStage = new Stage();
            mPopupStage.setResizable(false);
            mPopupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(loader.load(SceneNavigator.class.getResource(fxml).openStream()));
            mPopupStage.setScene(popupScene);
            mPopupStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    public static void showPopupWindow(String fxml, HashMap map) {
        try {
            FXMLLoader loader = new FXMLLoader();

            mPopupStage = new Stage();
            mPopupStage.setResizable(false);
            mPopupStage.initModality(Modality.APPLICATION_MODAL);

            Scene popupScene = new Scene(loader.load(SceneNavigator.class.getResource(fxml).openStream()));
            ((ParametrizedController)loader.getController()).setParameters(map);
            mPopupStage.setScene(popupScene);
            mPopupStage.show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
