package main.controller;

import main.application.App;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import main.model.*;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * ViewTopicController main.controller. This is injected using JavaFX from main.view/view_topic.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class ViewTopicController implements ParametrizedController<String, OMTopic>, RemoteEventListener {

    @FXML
    Label topicTitle;

    @FXML
    TextField sendMessage;

    @FXML
    ListView chat;

    @FXML
    ComboBox privateCMB;

    private RemoteEventListener mStub;
    private ObservableList<String> mMessageItems;
    private HashMap<String, OMTopic> mMap;

    @FXML
    public void initialize(){


        sendMessage.setOnKeyPressed((event) -> {
            if(event.getCode().impl_getCode() == KeyEvent.VK_ENTER) {
                sendButton();
            }
        });

        privateCMB.getItems().setAll("Public", "Private");

        privateCMB.getSelectionModel().select(0);

        mMessageItems = FXCollections.observableArrayList();
        chat.setItems(mMessageItems);
    }

    @Override
    public void setParameters(HashMap<String, OMTopic> map) {
        mMap = map;
        OMTopic topic = map.get("topic");
        topicTitle.setText(topic.title);
        listenForMessages();
        mMessageItems.clear();
        getAllComments();
        getPrivateComments();
    }

    /**
     *
     * Sets the map without initializing
     *
     * @param map {@link HashMap}
     */
    public void setMap(HashMap<String, OMTopic> map) {
        mMap = map;
    }

    /**
     *
     * Injected using JavaFX in view_topic.fxml
     *
     */
    public void readTopics() {
        SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
    }


    /**
     *
     * Injected using JavaFX in view_topic.fxml
     *
     */
    public void sendButton(){
        if(privateCMB.getSelectionModel().isSelected(0)) {
            sendMessage(sendMessage.getText());
        } else {
            sendPrivateMessage(sendMessage.getText());
        }
        sendMessage.setText("");
    }

    /**
     *
     * Sends a public message @link OMComment#privateMessage} = true to the {@link net.jini.space.JavaSpace}
     *
     * @param message {@link String} the message content sent to the space
     */
    public void sendMessage(String message) {
        if(message.trim().equals("") || message.isEmpty()){
            return;
        }
        //------- TRANSACTION -------
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(App.mTransactionManager, 3000);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);
        }

        //------- BEG OF TRANSACTION -------
        Transaction txn = trc.transaction;
        try{

            OMComment comment = new OMComment(message, false, 0, App.mUser.userid, mMap.get("topic").id);
            App.mSpace.write(comment, txn, 1000 * 60 * 5);

            OMNotificationRegister registerTemplate = new OMNotificationRegister();
            registerTemplate.topicId = mMap.get("topic").id;

            try {
                OMTopic topic = (OMTopic) App.mSpace.read(mMap.get("topic"), txn, 1000);
                if (topic != null) {//Check if the topic still exists, or has it been deleted.
                    MatchSet set = ((JavaSpace05) App.mSpace).contents(Collections.singletonList(registerTemplate), txn,
                            1000 * 5, Long.MAX_VALUE);

                    //------- SEND A NOTIFICATION TO ALL REGISTERED USERS -------
                    if (set != null) {
                        OMNotificationRegister register = (OMNotificationRegister) set.next();
                        ArrayList<OMNotification> notifications = new ArrayList<>();

                        while (register != null) {
                            if (!register.userId.equals(App.mUser.userid)) {

                                OMNotification notification = new OMNotification();
                                notification.userId = register.userId;
                                notification.topicName = mMap.get("topic").title;
                                notification.comment = comment;
                                notification.delete = false;
                                notification.topicId = mMap.get("topic").id;

                                notifications.add(notification);
                            }

                            register = (OMNotificationRegister) set.next();
                        }
                        if (!notifications.isEmpty()) {
                            ArrayList<Long> leaseDurations = new ArrayList<>(Collections.nCopies(notifications.size(),
                                    (long) (1000 * 60 * 30)));

                            ((JavaSpace05) App.mSpace).write(notifications, txn, leaseDurations);
                        }
                    }
                } else {
                    SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
                    SceneNavigator.showBasicPopupWindow("The topic has been deleted. Your message could not be sent.");
                }
                App.mLease.renew(1000*60*10);
                txn.commit();
                //------- END OF TRANSACTION -------
            } catch (ExceptionInInitializerError | NoClassDefFoundError e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                txn.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     *
     * Send a private message {@link OMComment#privateMessage} = true to the {@link net.jini.space.JavaSpace}
     *
     * @param message {@link String} sends a message to the space
     */
    public void sendPrivateMessage(String message){
        if(message.trim().equals("") || message.isEmpty()){
            return;
        }
        //------- TRANSACTION -------
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(App.mTransactionManager, 3000);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);
        }

        //------- BEG OF TRANSACTION -------
        Transaction txn = trc.transaction;
        try {
                try {
                    OMTopic topic = (OMTopic) App.mSpace.read(mMap.get("topic"), txn, 1000);
                    if(topic != null) {
                        OMComment comment = new OMComment(message, true, 0, App.mUser.userid, mMap.get("topic").id);
                        App.mSpace.write(comment, txn, 1000 * 60 * 30);

                        if (!App.mUser.userid.equals(mMap.get("topic").owner)) {
                            OMNotificationRegister registerTemplate = new OMNotificationRegister();
                            registerTemplate.topicId = mMap.get("topic").id;
                            registerTemplate.userId = App.mUser.userid;
                            registerTemplate.userId = mMap.get("topic").owner;

                            OMNotificationRegister register = (OMNotificationRegister) App.mSpace.read(registerTemplate, txn, 1000);

                            if (register != null) {//NOTIFY THE OWNER ONLY AND ONLY IF HE IS REGISTERED TO RECEIVE NOTIFICATIONS
                                OMNotification notification = new OMNotification();
                                notification.userId = mMap.get("topic").owner;
                                notification.topicName = mMap.get("topic").title;
                                notification.comment = comment;
                                notification.delete = false;
                                notification.topicId = mMap.get("topic").id;

                                App.mSpace.write(notification, txn, 1000 * 60 * 30);
                            }
                        }
                        App.mLease.renew(1000 * 60 * 10);
                    } else {
                        SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
                        SceneNavigator.showBasicPopupWindow("The topic has been deleted. Your message could not be sent.");
                    }
                    //------- END OF TRANSACTION -------
                    txn.commit();
                } catch (ExceptionInInitializerError | NoClassDefFoundError e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    txn.abort();
                }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     *
     * Listen for notifications about a new {@link OMComment} arriving
     *
     */
    private void listenForMessages(){
        // create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'mStub'
            mStub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            OMComment template = new OMComment();
            App.mSpace.notify(template, null, this.mStub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(RemoteEvent ev) {
        Platform.runLater(() -> {
            mMessageItems.clear();
            getAllComments();
            getPrivateComments();
        });
    }

    /**
     *
     * Gets all the {@link OMComment} that are {@link OMComment#privateMessage} = false from {@link net.jini.space.JavaSpace}
     *
     */
    private void getAllComments(){
        OMComment template = new OMComment();
        template.privateMessage = false;
        template.topicId = mMap.get("topic").id;

        try {
            MatchSet result = ((JavaSpace05)App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)),
                    null, 1000*2, Long.MAX_VALUE);
            if (result != null)  {
                ArrayList<String> messages = new ArrayList<>();
                OMComment comment = (OMComment) result.next();
                while (comment != null) {
                    messages.add(comment.owner + ": " + comment.content);
                    comment = (OMComment) result.next();
                }
               mMessageItems.addAll(messages);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * Gets all {@link OMComment} that are @link OMComment#privateMessage} = true from {@link net.jini.space.JavaSpace}
     *
     */
    private void getPrivateComments(){
        OMComment template = new OMComment();
        template.privateMessage = true;
        template.topicId = mMap.get("topic").id;
        if(!mMap.get("topic").owner.equals(App.mUser.userid)) {
            template.owner = App.mUser.userid;
        }

        try {
            MatchSet result = ((JavaSpace05)App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)),
                    null, 1000*2, Long.MAX_VALUE);
            if (result != null)  {
                ArrayList<String> messages = new ArrayList<>();
                OMComment comment = (OMComment) result.next();
                while (comment != null) {
                    messages.add(comment.owner + ": " + comment.content);
                    comment = (OMComment) result.next();
                }
                mMessageItems.addAll(messages);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
