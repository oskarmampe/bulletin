package controller;

import application.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.OMPost;
import model.OMTopic;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ViewTopicController implements ParametrizedController<String, OMTopic> {


    HashMap<String, OMTopic> mMap;

    @FXML
    Label topicTitle;

    @FXML
    ListView postList;

    ArrayList<OMPost> posts = new ArrayList<>();

    ObservableList<String> items;

    @FXML
    public void initialize(){
        items = FXCollections.observableArrayList();

        postList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

        });

        postList.setItems(items);
    }

    @Override
    public void setParamters(HashMap<String, OMTopic> map) {
        items.clear();
        mMap = map;
        OMTopic topic = map.get("topic");
        topicTitle.setText(topic.title);

        try{
            OMPost template = new OMPost();
            template.topicId = topic.id;

            MatchSet get = ((JavaSpace05) App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)), null, 1000 * 5, Long.MAX_VALUE);
            if (get == null) {

            } else {
                OMPost post = (OMPost) get.next();
                while (post != null) {
                    posts.add(post);
                    items.add(post.title);
                    post = (OMPost) get.next();
                }

            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UnusableEntryException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        }
    }

    public void addPost(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_POST, mMap);
    }

    public void readTopics() {
        SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
    }
}
