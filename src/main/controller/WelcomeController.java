package main.controller;

import main.application.App;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.application.HashPassword;
import main.model.OMLoggedInUser;
import main.model.OMUser;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class WelcomeController {

    @FXML
    TextField username;

    @FXML
    PasswordField password;


    public WelcomeController() {

    }

    public void onLoginClick() {
        login(username.getText(), password.getText());
    }

    public void login(String username, String password) {
        if(getUserFromSpace(username, password)) {
            SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
        } else {
            SceneNavigator.showBasicPopupWindow("Invalid credentials.");
        }
    }

    public boolean getUserFromSpace(String username, String password) {
        OMUser template = new OMUser();
        OMLoggedInUser loggedInTemplate = new OMLoggedInUser();

        template.userid = username;
        loggedInTemplate.userId = username;
        try {
            OMUser user = (OMUser) App.mSpace.read(template, null, 1000*2);
            if(user != null) {
                OMLoggedInUser loggedInUser = (OMLoggedInUser) App.mSpace.read(loggedInTemplate, null, 1000);
                if (loggedInUser != null ){
                    return false;
                }
                if(HashPassword.convertToHash(password, user.salt).equals(user.password)) {
                    App.mUser = user;
                    OMLoggedInUser newLogIn = new OMLoggedInUser();
                    newLogIn.userId = user.userid;
                    App.mLease = App.mSpace.write(newLogIn, null, 1000*60*4);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void navigateCreateUser(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_USER);
    }
}
