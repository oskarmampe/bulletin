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

/**
 *
 * WelcomeController main.controller. This is injected using JavaFX from main.view/welcome.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class WelcomeController {

    @FXML
    TextField username;

    @FXML
    PasswordField password;


    public WelcomeController() {
        //REQUIRED EMPTY CONSTRUCTOR FOR SCENENAVIGATOR
    }

    /**
     *
     * Injected using JavaFX in welcome.fxml
     *
     */
    public void onLoginClick() {
        if(getUserFromSpace(username.getText(), password.getText())) {
            SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
        } else {
            SceneNavigator.showBasicPopupWindow("Invalid credentials.");
        }
    }

    /**
     *
     * Gets the user from the {@link net.jini.space.JavaSpace}
     *
     * @param username {@link String} the username of the {@link OMUser}
     * @param password {@link String} the password of the {@link OMUser}
     * @return {@link Boolean} whether ot nor the user was retrieved from the space
     */
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
                    SceneNavigator.showBasicPopupWindow("The user is already logged in.");
                }
                if(HashPassword.convertToHash(password, user.salt).equals(user.password)) {
                    App.mUser = user;
                    OMLoggedInUser newLogIn = new OMLoggedInUser();
                    newLogIn.userId = user.userid;
                    App.mLease = App.mSpace.write(newLogIn, null, 1000*60*10);
                    return true;
                }
            }
        }catch (ExceptionInInitializerError | NoClassDefFoundError e1) {
            e1.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * Injected using JavaFX in welcome.fxml
     *
     */
    public void navigateCreateUser(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_USER);
    }
}
