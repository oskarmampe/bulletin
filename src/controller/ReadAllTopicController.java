package controller;

import application.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.OMTopic;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReadAllTopicController {

    @FXML
    ListView listView;

    ObservableList<String> items;

    @FXML
    public void initialize() {
        items = FXCollections.observableArrayList();

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            viewTopic((String)newValue);
        });

        listView.setItems(items);

        readAllPosts();
    }


    public void readAllPosts() {
        OMTopic template = new OMTopic();
        try {
            MatchSet get = ((JavaSpace05)App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)), null, 1000 * 5, Long.MAX_VALUE);
            if (get == null) {

            } else {
                OMTopic topic = (OMTopic) get.next();
                while (topic != null) {
                    items.add(topic.title);
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


    public void createTopic(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_TOPIC);
    }

    public void viewTopic(String value){
        OMTopic template = new OMTopic();
        template.title = value;
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(template, null, 1000*5);
            HashMap<String, OMTopic> topicHashMap = new HashMap<>();
            topicHashMap.put("topic", topic);
            SceneNavigator.loadScene(SceneNavigator.VIEW_TOPIC, topicHashMap);
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

}
