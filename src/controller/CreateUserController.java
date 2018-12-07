package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import model.OMUser;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import renderer.UserComboCell;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CreateUserController {

    @FXML
    ComboBox imagesCmb;

    @FXML
    TextField usernameTxt;

    @FXML
    TextField passwordTxt;


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

    public void createUser()throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();

        OMUser template = new OMUser();
        template.userid = usernameTxt.getText();
        template.password = passwordTxt.getText();
        template.image = (String) imagesCmb.getSelectionModel().getSelectedItem();

        try {
            OMUser user = (OMUser) App.mSpace.read(template, null, 1000*2);

            if(user == null) {
                System.out.println("Creating user");
                App.mSpace.write(template, null, 1000*60*5);
                App.user = template;

                SceneNavigator.loadScene(SceneNavigator.CREATE_TOPIC);
            } else {
                return;
            }



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

    public void navigateWelcome(){
        SceneNavigator.loadScene(SceneNavigator.WELCOME);
    }
}
