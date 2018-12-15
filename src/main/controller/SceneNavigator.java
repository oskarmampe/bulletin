package main.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.jini.core.entry.Entry;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 * SceneNavigator main.controller. This class is used to switch scenes. Holds all possible views to navigate to.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class SceneNavigator {

    public static final String MAIN = "../view/main.fxml";
    public static final String WELCOME = "../view/welcome.fxml";
    public static final String READ_ALL_TOPICS = "../view/read_topics.fxml";
    public static final String VIEW_TOPIC = "../view/view_topic.fxml";
    public static final String CREATE_USER = "../view/create_user.fxml";
    public static final String BASIC_POPUP = "../view/popup_basic.fxml";
    public static final String CREATE_TOPIC_POPUP = "../view/popup_create_topic.fxml";
    public static final String EDIT_TOPIC_POPUP = "../view/popup_edit_topic.fxml";

    private static MainController mMainController;
    public static Stage mPrimaryStage;//The primary stage that the user sees
    private static Stage mPopupStage;//The stage of the popup that comes up.

    /**
     *
     * Sets the main controller.
     *
     * @param mainController the {@link MainController} injected into main.view/main.fxml
     */
    public static void setMainController(MainController mainController) {
        SceneNavigator.mMainController = mainController;
    }


    /**
     *
     * Loads an fxml view.
     *
     * @param fxml {@link String} resource path to the fxml file
     */
    public static void loadScene(String fxml) {
        try {
            mMainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Loads an fxml view and puts a {@link HashMap} object into it
     *
     * @param fxml {@link String} resource path to the fxml file
     * @param map {@link HashMap} containing a {@link net.jini.core.entry.Entry}
     */
    public static void loadScene(String fxml, HashMap map) {
        try {
            FXMLLoader loader = new FXMLLoader();
            mMainController.setScene(loader.load(SceneNavigator.class.getResource(fxml).openStream()));
            ParametrizedController controller = loader.getController();
            controller.setParameters(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Shows a basic popup window with a label.
     *
     * @param labelText {@link String} text of a label
     */
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

    /**
     *
     * Close the popup window.
     *
     */
    public static void closePopupWindow() {
        mPopupStage.close();
    }

    /**
     *
     * Show a popup window.
     *
     * @param fxml {@link String} resource path to the fxml file
     */
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

    /**
     *
     * Shows a popup window with a Map containing {@link Entry} to show.
     *
     * @param fxml
     * @param map
     */
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
