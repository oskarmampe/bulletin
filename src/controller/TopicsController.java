package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMObj;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;

import java.rmi.RemoteException;

public class TopicsController {

    @FXML
    TextField receiveField;

    public void testActionReceive() {
        OMObj template = new OMObj();
        try {
            OMObj get = (OMObj) App.mSpace.take(template, null, 1000 * 5);
            if (get == null) {
                receiveField.setText("No object found");
            } else {
                receiveField.setText(get.contents);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnusableEntryException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        }
    }

    public void changeScene(){
        SceneNavigator.loadScene(SceneNavigator.SEND);
    }
}
