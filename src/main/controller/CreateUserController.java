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

/**
 *
 * CreateUserController in main.controller. This is injected using JavaFX from main.view/create_user.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
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

    /**
     *
     * Injected into JavaFX using create_user.fxml. Navigates to the correct scene if credentials are correct.
     *
     */
    public void onCreateUserButton(){
        if(createUser(usernameTxt.getText(), passwordTxt.getText(), (String) imagesCmb.getSelectionModel().getSelectedItem())){
            SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
        } else {
            SceneNavigator.showBasicPopupWindow("Invalid credentials.");
        }
    }

    /**
     *
     * Create user method, which writes a user into space.
     *
     * @param username username of the {@link OMUser} class
     * @param password password of the {@link OMUser} class
     * @param image image of the {@link OMUser} class
     * @return {@link Boolean} that corresponds whether the creation was successful or not.
     */
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

                    App.mLease = App.mSpace.write(loggedInUser, txn, 1000*60*10);//Keeps track of how long the user is seen as logged in.
                } else {
                    txn.abort();
                    return false;
                }
                txn.commit();
                //------- END OF TRANSACTION -------
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

    /**
     *
     * Injected into JavaFX using create_user.fxml. Navigate to welcome screen.
     *
     */
    public void navigateWelcome(){
        SceneNavigator.loadScene(SceneNavigator.WELCOME);
    }
}
