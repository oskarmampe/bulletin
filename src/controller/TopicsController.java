package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMObj;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.entry.UnusableEntriesException;
import net.jini.space.JavaSpace05;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class TopicsController {

    @FXML
    TextField receiveField;

    public void testActionReceive() {
        OMObj template = new OMObj();
        try {
            LinkedList<OMObj> get = (LinkedList<OMObj>) ((JavaSpace05)App.mSpace).take(new ArrayList<OMObj>(Arrays.asList(template)), null, 1000 * 5, Long.MAX_VALUE);
            if (get == null) {
                receiveField.setText("No object found");
            } else {
                for (OMObj obj : get) {
                   receiveField.setText(receiveField.getText().concat(obj.contents));
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (UnusableEntriesException e) {
            e.printStackTrace();
        }
    }

    public void changeScene(){
        SceneNavigator.loadScene(SceneNavigator.SEND);
    }
}
