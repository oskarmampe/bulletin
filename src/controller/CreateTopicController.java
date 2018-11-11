package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMTopic;
import net.jini.core.transaction.TransactionException;

import java.rmi.RemoteException;

public class CreateTopicController {
    @FXML
    TextField objTextField;

    int count = 0;

    public void createTopic() {
        OMTopic obj = new OMTopic(App.user.userid+count+objTextField.getText(), count, App.user.userid, objTextField.getText());
        count++;
        try {
            App.mSpace.write(obj, null, 1000 * 60);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void changeScene(){
        SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
    }
}
