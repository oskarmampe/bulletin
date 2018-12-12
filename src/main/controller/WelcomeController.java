package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.OMLoggedInUser;
import model.OMUser;

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

    public void login() {
        OMUser template = new OMUser();
        OMLoggedInUser loggedInTemplate = new OMLoggedInUser();

        template.userid = username.getText();
        loggedInTemplate.userId = username.getText();
        try {
            OMUser user = (OMUser) App.mSpace.read(template, null, 1000*2);
            OMLoggedInUser loggedInUser = (OMLoggedInUser) App.mSpace.read(loggedInTemplate, null, 1000);
            if (loggedInUser != null ){
                SceneNavigator.showBasicPopupWindow("User already logged in.");
                return;
            }
            if(user != null) {
                KeySpec spec = new PBEKeySpec(password.getText().toCharArray(), user.salt, 65536, 128);
                SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                byte[] hash = f.generateSecret(spec).getEncoded();
                Base64.Encoder enc = Base64.getEncoder();

                if(enc.encodeToString(hash).equals(user.password)) {
                    App.mUser = user;
                    SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
                    OMLoggedInUser newLogIn = new OMLoggedInUser();
                    newLogIn.userId = user.userid;
                    App.mLease = App.mSpace.write(newLogIn, null, 1000*60*4);

                } else {
                    SceneNavigator.showBasicPopupWindow("Invalid credentials.");
                }
            } else {
                SceneNavigator.showBasicPopupWindow("Invalid credentials.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void navigateCreateUser(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_USER);
    }
}
