package controller;

import application.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.OMTopic;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.entry.UnusableEntriesException;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class ReadAllTopicController {

    @FXML
    ListView listView;

    ObservableList<String> items;

    @FXML
    public void initialize() {
        items = FXCollections.observableArrayList();
        listView.setItems(items);

        readAllPosts();
    }


    public void readAllPosts() {
        OMTopic template = new OMTopic();
        try {
            MatchSet get = ((JavaSpace05)App.mSpace).contents(new ArrayList<OMTopic>(Arrays.asList(template)), null, 1000 * 5, Long.MAX_VALUE);
            if (get == null) {

            } else {
                OMTopic topic = (OMTopic) get.next();
                while (topic != null) {
                    items.add("Topic: " + topic.title + " Owner: " + topic.owner);
                    topic = (OMTopic) get.next();
                }

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (UnusableEntryException e) {
            e.printStackTrace();
        }
    }


    public void changeScene(){
        SceneNavigator.loadScene(SceneNavigator.SEND);
    }

}
