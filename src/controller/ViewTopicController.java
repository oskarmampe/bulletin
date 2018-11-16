package controller;

import application.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.OMComment;
import model.OMPost;
import model.OMTopic;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ViewTopicController implements ParametrizedController<String, OMTopic>, RemoteEventListener {

    @FXML
    Label topicTitle;

    @FXML
    ListView postList;

    @FXML
    TextField sendMessage;

    @FXML
    ListView chat;

    private ArrayList<OMPost> posts = new ArrayList<>();
    private RemoteEventListener theStub;
    private ObservableList<String> postItems;
    private ObservableList<String> messageItems;
    private HashMap<String, OMTopic> mMap;

    @FXML
    public void initialize(){
        postItems = FXCollections.observableArrayList();
        postList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

        });
        messageItems = FXCollections.observableArrayList();
        chat.setItems(messageItems);
        postList.setItems(postItems);
    }

    @Override
    public void setParamters(HashMap<String, OMTopic> map) {
        postItems.clear();
        mMap = map;
        OMTopic topic = map.get("topic");
        topicTitle.setText(topic.title);

        try{
            OMPost template = new OMPost();
            template.topicId = topic.id;

            MatchSet get = ((JavaSpace05) App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)),
                    null, 1000 * 5, Long.MAX_VALUE);
            if (get != null) {
                OMPost post = (OMPost) get.next();
                while (post != null) {
                    posts.add(post);
                    postItems.add(post.title);
                    post = (OMPost) get.next();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        listenForMessages();
        chat.getItems().clear();
        getAllComments();
        getPrivateComments();
    }

    public void addPost(){
        SceneNavigator.loadScene(SceneNavigator.CREATE_POST, mMap);
    }

    public void readTopics() {
        SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
    }

    public void sendMessage(){
        try{
            OMComment comment = new OMComment(sendMessage.getText(), false, 0, App.user.userid, mMap.get("topic").id);
            App.mSpace.write(comment, null, 1000 * 60 * 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateMessage(){
        try{
            OMComment comment = new OMComment(sendMessage.getText(), true, 0, App.user.userid, mMap.get("topic").id);
            App.mSpace.write(comment, null, 1000 * 60 * 5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages(){
        // create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stu'
            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            OMComment template = new OMComment();
            App.mSpace.notify(template, null, this.theStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notify(RemoteEvent ev) {
        Platform.runLater(() -> {
            chat.getItems().clear();
            getAllComments();
            getPrivateComments();
        });
    }

    private void getAllComments(){
        // this is the method called when we are notified
        // of an object of interest
        OMComment template = new OMComment();
        template.privateMessage = false;
        template.topicId = mMap.get("topic").id;

        try {
            MatchSet result = ((JavaSpace05)App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)),
                    null, 1000*2, Long.MAX_VALUE);
            if (result == null) {

            } else {
                ArrayList<String> messages = new ArrayList<>();
                OMComment comment = (OMComment) result.next();
                while (comment != null) {
                    messages.add(comment.content);
                    comment = (OMComment) result.next();
                }
               chat.getItems().addAll(messages);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getPrivateComments(){
        OMComment template = new OMComment();
        template.privateMessage = true;
        template.topicId = mMap.get("topic").id;
        if(!mMap.get("topic").owner.equals(App.user.userid)) {
            template.owner = App.user.userid;
        }

        try {
            MatchSet result = ((JavaSpace05)App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)),
                    null, 1000*2, Long.MAX_VALUE);
            if (result == null) {

            } else {
                ArrayList<String> messages = new ArrayList<>();
                OMComment comment = (OMComment) result.next();
                while (comment != null) {
                    messages.add(comment.content);
                    comment = (OMComment) result.next();
                }
                chat.getItems().addAll(messages);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
