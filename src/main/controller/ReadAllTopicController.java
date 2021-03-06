package main.controller;

import main.application.App;
import main.callbacks.DeleteCallback;
import main.callbacks.EditCallback;
import main.callbacks.NotificationCallback;
import main.callbacks.ViewCallback;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.model.*;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.transaction.*;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.AvailabilityEvent;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.MarshalledObject;
import java.util.*;

/**
 *
 * Read all topic main.controller. This is injected using JavaFX from main.view/read_topics.fxml
 * Implements {@link net.jini.space.JavaSpace05#registerForAvailabilityEvent(Collection, Transaction, boolean,
 * RemoteEventListener, long, MarshalledObject)} to wait for any new {@link OMNotification} or {@link OMTopic} to arrive.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see RemoteEventListener
 */
public class ReadAllTopicController implements RemoteEventListener{


    //------- FXML Elements -------
    @FXML
    TableView<OMTopic> topicTable;

    @FXML
    ImageView userIcon;

    @FXML
    Label userLabel;

    @FXML
    MenuBar notificationsMenu;

    @FXML
    Button createTopicButton;

    @FXML
    Label addText;

    //------- Lists for ListView -------
    private ObservableList<String> mNotificationsValue;
    private ObservableList<OMTopic> mTopics;

    @FXML
    public void initialize() {
        mTopics = FXCollections.observableArrayList();

        topicTable.getStyleClass().add("noheader");

        createTopicButton.setGraphic(new ImageView(new Image("main/resources/images/icons/add.png")));

        userIcon.setImage(new Image(App.mUser.image));
        userLabel.setText("Welcome, " + App.mUser.userid);

        initializeMenu();
        listenForMessages();
        initializeTable();
        readAllTopics();
    }

    /**
     *
     * Initializes {@link ReadAllTopicController#notificationsMenu} with values from {@link JavaSpace05}
     *
     */
    private void initializeMenu() {
        mNotificationsValue = FXCollections.observableArrayList();

        getNotifications();

        ListView<String> listView = new ListView<>(mNotificationsValue);
        listView.setPrefHeight(100);
        CustomMenuItem listMenuItem = new CustomMenuItem(listView, false);

        //Notifications are a menu item which contains a ListView inside. Any new notifications are added to the ListView.
        Menu listMenu = new Menu("Notifications: " + listView.getItems().size());
        listMenu.getItems().add(listMenuItem);
        notificationsMenu.getMenus().addAll(listMenu);
    }

    /**
     *
     * Get all {@link OMNotification} for the user.
     *
     */
    public MatchSet getNotifications() {
        try { //Single operation so no transaction
            OMNotification notificationTemplate = new OMNotification();
            notificationTemplate.userId = App.mUser.userid;
            return ((JavaSpace05)App.mSpace).contents(Collections.singletonList(notificationTemplate), null,
                    1000*2, Long.MAX_VALUE);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * Initializes the {@link ReadAllTopicController#topicTable} which holds all the topics. It also holds the icons
     * and each icon is stored in a separate column with its own main.renderer.
     *
     */
    private void initializeTable() {

        //------- All Columns -------
        TableColumn<OMTopic, String> textColumn = new TableColumn<>();
        TableColumn<OMTopic, String> viewColumn = new TableColumn<>();
        TableColumn<OMTopic, String> editColumn = new TableColumn<>();
        TableColumn<OMTopic, String> deleteColumn = new TableColumn<>();
        TableColumn<OMTopic, String> notificationColumn = new TableColumn<>();

        //------- Editing Column Properties -------
        textColumn.setEditable(false);
        viewColumn.setEditable(false);
        editColumn.setEditable(false);
        deleteColumn.setEditable(false);
        notificationColumn.setEditable(false);

        textColumn.setResizable(false);
        viewColumn.setResizable(false);
        editColumn.setResizable(false);
        deleteColumn.setResizable(false);
        notificationColumn.setResizable(false);

        textColumn.setPrefWidth(690);
        viewColumn.setPrefWidth(24);
        editColumn.setPrefWidth(24);
        deleteColumn.setPrefWidth(24);
        notificationColumn.setPrefWidth(24);

        viewColumn.setCellFactory(new ViewCallback());
        editColumn.setCellFactory(new EditCallback());
        deleteColumn.setCellFactory(new DeleteCallback());
        notificationColumn.setCellFactory(new NotificationCallback());

        textColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title));
        mTopics = topicTable.getItems();

        topicTable.getColumns().addAll(textColumn, viewColumn, editColumn, deleteColumn, notificationColumn);
    }


    /**
     *
     * Read all topics stored in the {@link net.jini.space.JavaSpace}.
     *
     */
    private void readAllTopics() {
        OMTopic template = new OMTopic();
        try {
            MatchSet set = ((JavaSpace05)App.mSpace).contents(new ArrayList<>(Collections.singletonList(template)),
                    null, 1000 * 2, Long.MAX_VALUE);
            if (set != null) {
                OMTopic topic = (OMTopic) set.next();
                while (topic != null) {
                    mTopics.add(topic);
                    topic = (OMTopic) set.next();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Method responsible for registering for notifications from {@link RemoteEventListener}.
     * This method is waiting for {@link OMTopic}, {@link OMTopicCounter} and {@link OMNotification}
     *
     */
    public void listenForMessages(){
        // create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stu'
            RemoteEventListener stub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            OMTopicCounter template = new OMTopicCounter();
            OMNotification notification = new OMNotification();
            OMTopic topicTemplate = new OMTopic();

            notification.userId = App.mUser.userid;

            ArrayList<Entry> templates = new ArrayList<>();

            templates.add(template);
            templates.add(notification);
            templates.add(topicTemplate);

            ((JavaSpace05)App.mSpace).registerForAvailabilityEvent(templates, null, false, stub,
                    1000*60*30, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(RemoteEvent ev) {
        AvailabilityEvent event = (AvailabilityEvent) ev;

            Entry entry = null;

            try {
                entry = event.getEntry();
            } catch (UnusableEntryException e1) {
                e1.printStackTrace();
            }

            if (entry instanceof OMTopicCounter) {

                OMTopic template = new OMTopic();
                try {
                    MatchSet set = ((JavaSpace05) App.mSpace).contents(Collections.singletonList(template),
                            null, 1000 * 2, Long.MAX_VALUE);

                    if (set != null) {
                        OMTopic topic = (OMTopic) set.next();
                        ArrayList<OMTopic> topics = new ArrayList<>(mTopics);
                        //Find all the topics. Any topics that do not exist is the newly arrived list, remove from the
                        //list view.
                        while (topic != null) {
                            if (!mTopics.contains(topic)) {
                                mTopics.add(topic);
                            } else {
                                topics.remove(topic);
                            }

                            topic = (OMTopic) set.next();
                        }

                        mTopics.removeAll(topics);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (entry instanceof OMNotification) {
               addNotification(((OMNotification) entry).comment != null ? ((OMNotification) entry).comment.owner : "", ((OMNotification) entry).topicName,
                       ((OMNotification) entry).comment != null ? ((OMNotification) entry).comment.privateMessage : false, ((OMNotification) entry).delete);
            } else if (entry instanceof OMTopic) {//If a new topic arrived, then it was just added to the space, or edited.
                for (OMTopic topic : mTopics) {
                    if(topic.index.intValue() == ((OMTopic) entry).index.intValue()
                            && topic.id.equals(((OMTopic) entry).id) && topic.owner.equals(((OMTopic) entry).owner)){
                        topic.title = ((OMTopic) entry).title;
                        topicTable.refresh();
                    }
                }
            }

    }

    /**
     *
     * Injected into JavaFX using read_topics.fxml.
     *
     */
    public void createUser() {
        SceneNavigator.showPopupWindow(SceneNavigator.CREATE_TOPIC_POPUP);
    }

    /**
     *
     * Method for displaying the notifications into the menu. Its a method so it doesn't have to be change in multiple places.
     *
     * @param userId {@link String} the id of the user
     * @param topicName {@link String} the topic name
     * @param type {@link Boolean} type of the comment that arrived, private or public
     * @param delete {@link Boolean} whether the notification is about delete or not
     */
    private void addNotification(String userId, String topicName, boolean type, boolean delete) {
        Platform.runLater(() -> {//Needed to get rid of a JavaFX rendering error
            if (!delete) {
                mNotificationsValue.add("User " + userId + " has posted a " + (type ? "private" : "public") + " message in " + topicName);
            } else {
                mNotificationsValue.add(topicName + " has been deleted, along with all of it's messages.");
            }
            notificationsMenu.getMenus().get(0).setText("Notifications: " + mNotificationsValue.size());
        });

    }

    /**
     *
     * Logs the user out and navigates back to the welcome screen.
     *
     */
    public void logout() {
        try {
            OMLoggedInUser template = new OMLoggedInUser();
            template.userId = App.mUser.userid;

            App.mSpace.take(template, null, 1000);

            App.mUser = null;
            SceneNavigator.loadScene(SceneNavigator.WELCOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
