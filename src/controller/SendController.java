package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMObj;
import net.jini.core.transaction.TransactionException;

import java.rmi.RemoteException;

public class SendController {

    @FXML
    TextField objTextField;

    public void testActionSend() {
        OMObj obj = new OMObj(objTextField.getText());
        try {
            App.mSpace.write(obj, null, 1000 * 60);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void changeScene(){
        SceneNavigator.loadScene(SceneNavigator.TOPICS);
    }
}
