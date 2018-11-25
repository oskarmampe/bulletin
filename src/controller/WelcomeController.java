package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.OMUser;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class WelcomeController {

    @FXML
    TextField username;

    @FXML
    PasswordField password;


    public WelcomeController() {

    }

    public void login() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();

        OMUser template = new OMUser();
        template.userid = username.getText();
        template.password = password.getText();

        try {
            OMUser user = (OMUser) App.mSpace.read(template, null, 1000*2);

            if(user == null) {
                App.mSpace.write(template, null, 1000*60*5);
                App.user = template;
            } else {
                App.user = user;
            }

            SceneNavigator.loadScene(SceneNavigator.CREATE_TOPIC);


        } catch (UnusableEntryException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    public void navigateCreateUser(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_USER);
    }
}
