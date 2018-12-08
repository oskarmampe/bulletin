package controller;

import application.App;
import callbacks.DeleteCallback;
import callbacks.EditCallback;
import callbacks.NotificationCallback;
import callbacks.ViewCallback;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.*;
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
 * Read all topic controller. This is injected using JavaFX from view/read_topics.fxml
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

        createTopicButton.setGraphic(new ImageView(new Image("resources/images/icons/add.png")));

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
    private void getNotifications() {
        try { //Single operation so no transaction
            OMNotification notificationTemplate = new OMNotification();
            notificationTemplate.userId = App.mUser.userid;
            MatchSet set = ((JavaSpace05)App.mSpace).contents(Collections.singletonList(notificationTemplate), null,
                    1000*2, Long.MAX_VALUE);

            if (set != null) {
                OMNotification notification = (OMNotification) set.next();
                while (notification != null){
                    //mNotificationsValue being the items in ListView inside the notification menu item.
                    addNotification(notification.comment.owner, notification.topicName, notification.comment.privateMessage);
                    notification = (OMNotification) set.next();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Initializes the {@link ReadAllTopicController#topicTable} which holds all the topics. It also holds the icons
     * and each icon is stored in a separate column with its own renderer.
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

    private void listenForMessages(){
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
        System.out.println("notified");
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
                System.out.println("notification arrived");
               addNotification(((OMNotification) entry).comment.owner, ((OMNotification) entry).topicName, ((OMNotification) entry).comment.privateMessage);
            } else if (entry instanceof OMTopic) {
                System.out.println("topic notifiyed");
                for (OMTopic topic : mTopics) {
                    System.out.println("topic nod");
                    if(topic.index.intValue() == ((OMTopic) entry).index.intValue()
                            && topic.id.equals(((OMTopic) entry).id) && topic.owner.equals(((OMTopic) entry).owner)){
                        System.out.println("topic");
                        topic.title = ((OMTopic) entry).title;
                        topicTable.refresh();
                    }
                }
            }

    }

    public void createUser() {
        SceneNavigator.showPopupWindow(SceneNavigator.CREATE_TOPIC_POPUP);
    }

    private void addNotification(String userId, String topicName, boolean type) {
        Platform.runLater(() -> {
            mNotificationsValue.add( "User " + userId + " has posted a " + (type ? "private" : "public") + " message in " + topicName );
            notificationsMenu.getMenus().get(0).setText("Notifications: " + mNotificationsValue.size());
        });

    }


}
