package controller;

import application.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.OMComment;
import model.OMPost;
import model.OMTopic;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.*;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import javax.xml.soap.Text;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReadAllTopicController {

    @FXML
    ListView listView;

    @FXML
    TextField deleteTopicField;

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

    public void deleteTopic(){
        String title = (String) deleteTopicField.getText();
        OMTopic topicTemplate = null;

        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(App.mTransactionManager, 1000*5);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);;
        }

        Transaction txn = trc.transaction;

        for(OMTopic topic : topics) {
            if(topic.title.equals(title) && topic.owner.equals(App.user.userid)) {
                topicTemplate = topic;
            }
        }

        try{
            OMComment commentTemplate = new OMComment();
            commentTemplate.topicId = topicTemplate.id;

            ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(commentTemplate)), txn, 1000, Long.MAX_VALUE);

            OMPost postTemplate = new OMPost();
            postTemplate.topicId = topicTemplate.id;

            ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(postTemplate)), txn, 1000, Long.MAX_VALUE);

            ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(topicTemplate)), txn, 1000, Long.MAX_VALUE);


            txn.commit();

        } catch (Exception e){
            e.printStackTrace();
            try {
                txn.abort();
            } catch (UnknownTransactionException | CannotAbortException | RemoteException e1) {
                e1.printStackTrace();
            }
        }

    }



}
