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
    ArrayList<OMTopic> topics = new ArrayList<>();

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
                    topics.add(topic);
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
        OMTopic template = null;

        for (OMTopic topic : topics) {
            if(topic.title.equals(value)) {
                template = topic;
                break;
            }
        }
        HashMap<String, OMTopic> topicHashMap = new HashMap<>();
        topicHashMap.put("topic", template);
        SceneNavigator.loadScene(SceneNavigator.VIEW_TOPIC, topicHashMap);

    }

    public void sendMessage(){

    }

}
