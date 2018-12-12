package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.OMLoggedInUser;
import model.OMUser;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import renderer.UserComboCell;

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
        //------- TRANSACTION -------
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(App.mTransactionManager, 1000*5);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);
        }

        //------- BEG OF TRANSACTION -------
        Transaction txn = trc.transaction;
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(passwordTxt.getText().toCharArray(), salt, 65536, 128);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        try {
            OMUser template = new OMUser();
            template.userid = usernameTxt.getText();


            try {
                OMUser user = (OMUser) App.mSpace.read(template, txn, 1000);
                if (user == null) {
                    template.password = enc.encodeToString(hash);
                    template.salt = salt;
                    template.image = (String) imagesCmb.getSelectionModel().getSelectedItem();

                    App.mSpace.write(template, txn, 1000 * 60 * 30);
                    App.mUser = template;

                    OMLoggedInUser loggedInUser = new OMLoggedInUser();
                    loggedInUser.userId = template.userid;

                    App.mLease = App.mSpace.write(loggedInUser, txn, 1000*60*4);

                    SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
                } else {
                    SceneNavigator.showBasicPopupWindow("Username is taken. Please try again with a different username.");
                }
                txn.commit();
            } catch (Exception e) {
                e.printStackTrace();
                txn.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void navigateWelcome(){
        SceneNavigator.loadScene(SceneNavigator.WELCOME);
    }
}
