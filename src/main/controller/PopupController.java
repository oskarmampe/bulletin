package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 *
 * Popup Controller main.controller. This is injected using JavaFX from main.view/popup_basic.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class PopupController {

    @FXML
    Label textLabel;

    /**
     *
     * Injected into JavaFX using popup_basic.fxml.
     *
     */
    public void onOkButton() {
        SceneNavigator.closePopupWindow();
    }

    /**
     *
     * Injected into JavaFX using popup_basic.fxml.
     *
     */
    public void setLabelText(String text) {
        textLabel.setText(text);
    }
}
