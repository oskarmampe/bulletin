package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import renderer.UserComboCell;

import javax.swing.*;

public class CreateUserController {

    @FXML
    ComboBox imagesCmb;


    @FXML
    public void initialize(){
        String[] items = {
                "resources/images/icons/boy.png",
                "resources/images/icons/boy-1.png",
                "resources/images/icons/girl.png",
                "resources/images/icons/girl-1.png",
                "resources/images/icons/man.png",
                "resources/images/icons/man-1.png",
                "resources/images/icons/man-2.png",
                "resources/images/icons/man-3.png",
                "resources/images/icons/man-4.png"};
        imagesCmb.getItems().setAll(items);
        imagesCmb.setCellFactory(c -> new UserComboCell());
        imagesCmb.setButtonCell(new UserComboCell());
        imagesCmb.getSelectionModel().select(0);
    }

    public void createUser(){


    }

    public void navigateWelcome(){
        SceneNavigator.loadScene(SceneNavigator.WELCOME);
    }
}
