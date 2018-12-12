package main.controller;

import main.application.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.application.HashPassword;
import main.model.OMLoggedInUser;
import main.model.OMUser;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import main.renderer.UserComboCell;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
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
    PasswordField passwordTxt;


    @FXML
    public void initialize(){
        String[] items = {
                "main/resources/images/icons/boy.png",
                "main/resources/images/icons/boy-1.png",
                "main/resources/images/icons/girl.png",
                "main/resources/images/icons/girl-1.png",
                "main/resources/images/icons/man.png",
                "main/resources/images/icons/man-1.png",
                "main/resources/images/icons/man-2.png",
                "main/resources/images/icons/man-3.png",
                "main/resources/images/icons/man-4.png"};
        imagesCmb.getItems().setAll(items);
        imagesCmb.setCellFactory(c -> new UserComboCell());
        imagesCmb.setButtonCell(new UserComboCell());
        imagesCmb.getSelectionModel().select(0);
    }

    public void onCreateUserButton(){
        if(createUser(usernameTxt.getText(), passwordTxt.getText(), (String) imagesCmb.getSelectionModel().getSelectedItem())){
            SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
        } else {
            SceneNavigator.showBasicPopupWindow("Invalid credentials.");
        }
    }

    public boolean createUser(String username, String password, String image) {
        if(username.trim().equals("") || username.isEmpty() || password.trim().equals("") || password.isEmpty()){
            return false;
        }
        //------- TRANSACTION -------
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(App.mTransactionManager, 1000*5);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);
            return false;
        }

        //------- BEG OF TRANSACTION -------
        Transaction txn = trc.transaction;
        try {
            OMUser template = new OMUser();
            template.userid = username;
            try {
                OMUser user = (OMUser) App.mSpace.read(template, txn, 1000);
                if (user == null) {
                    template.salt = HashPassword.generateRandomSalt();
                    template.password = HashPassword.convertToHash(password, template.salt);
                    template.image = image;

                    App.mSpace.write(template, txn, 1000 * 60 * 30);
                    App.mUser = template;

                    OMLoggedInUser loggedInUser = new OMLoggedInUser();
                    loggedInUser.userId = template.userid;

                    App.mLease = App.mSpace.write(loggedInUser, txn, 1000*60*4);
                } else {
                    txn.abort();
                    return false;
                }
                txn.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                txn.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void navigateWelcome(){
        SceneNavigator.loadScene(SceneNavigator.WELCOME);
    }
}
