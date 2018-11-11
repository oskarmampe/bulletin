package controller;


import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMPost;
import model.OMTopic;
import net.jini.core.transaction.TransactionException;

import java.rmi.RemoteException;
import java.util.HashMap;

public class CreatePostController implements ParametrizedController<String, OMTopic> {


    @FXML
    TextField objTextField;

    HashMap<String, OMTopic> mMap;

    int count = 0;

    public void createPost() {
        OMPost obj = new OMPost(objTextField.getText(), count, "", mMap.get("topic").id);
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
        SceneNavigator.loadScene(SceneNavigator.VIEW_TOPIC, mMap);
    }

    @Override
    public void setParamters(HashMap<String, OMTopic> map) {
        mMap = map;
    }
}
