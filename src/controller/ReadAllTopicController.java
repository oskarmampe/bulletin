package controller;

import application.App;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import model.OMComment;
import model.OMPost;
import model.OMTopic;
import model.OMTopicCounter;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.*;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;
import renderer.TopicListCell;
import renderer.UserComboCell;

import javax.xml.soap.Text;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.lang.invoke.ConstantCallSite;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public class ReadAllTopicController implements RemoteEventListener{

    @FXML
    TableView<OMTopic> topicTable;

    @FXML
    TextField deleteTopicField;

    @FXML
    ImageView userIcon;

    @FXML
    Label userLabel;

    ObservableList<OMTopic> items;
    ArrayList<OMTopic> topics = new ArrayList<>();
    RemoteEventListener stub;

    @FXML
    public void initialize() {
        items = FXCollections.observableArrayList();

        topicTable.getStyleClass().add("noheader");

        userIcon.setImage(new Image(App.user.image));

        listenForMessages();
        /*
                <columns>
          <TableColumn maxWidth="1.7976931348623157E308" minWidth="504.0" prefWidth="504.0" text="Text" />
          <TableColumn maxWidth="24.0" minWidth="24.0" prefWidth="24.0" text="View/Hide" />
            <TableColumn maxWidth="24.0" minWidth="24.0" prefWidth="24.0" text="Edit" />
            <TableColumn maxWidth="24.0" minWidth="24.0" prefWidth="24.0" text="Delete" />
            <TableColumn maxWidth="24.0" minWidth="24.0" prefWidth="24.0" text="Notifications" />
        </columns>
        */

        TableColumn<OMTopic, String> textColumn = new TableColumn<>();
        TableColumn<OMTopic, String> viewColumn = new TableColumn<>();
        TableColumn<OMTopic, String> editColumn = new TableColumn<>();
        TableColumn<OMTopic, String> deleteColumn = new TableColumn<>();
        TableColumn<OMTopic, String> notificationColumn = new TableColumn<>();

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



//
//        editColumn.setCellValueFactory(cellData -> new SimpleStringProperty("resources/images/icons/pencil-edit-button.png"));
//        notificationColumn.setCellValueFactory(cellData -> new SimpleStringProperty("resources/images/icons/blind.png"));

        Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> viewCellFactory =
                new Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>>() {
                    @Override
                    public TableCell call(final TableColumn<OMTopic, String> param) {
                        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

                            final Button btn = new Button();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        System.out.println( getTableView().getItems().get(getIndex()).owner );
                                    });
                                    ImageView imageView = new ImageView(new Image("resources/images/icons/view.png"));
                                    imageView.setFitHeight(18);
                                    imageView.setFitWidth(18);
                                    setMaxWidth(18);
                                    setAlignment(Pos.CENTER);
                                    btn.setGraphic(imageView);
                                    setGraphic(btn);
                                    btn.setBackground(Background.EMPTY);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> editCellFactory =
                new Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>>() {
                    @Override
                    public TableCell call(final TableColumn<OMTopic, String> param) {
                        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

                            final Button btn = new Button();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        System.out.println( getTableView().getItems().get(getIndex()).owner );
                                    });
                                    ImageView imageView = new ImageView(new Image("resources/images/icons/edit.png"));
                                    imageView.setFitHeight(18);
                                    imageView.setFitWidth(18);
                                    setMaxWidth(18);
                                    setAlignment(Pos.CENTER);
                                    btn.setGraphic(imageView);
                                    setGraphic(btn);
                                    btn.setBackground(Background.EMPTY);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> deleteCellFactory =
                new Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>>() {
                    @Override
                    public TableCell call(final TableColumn<OMTopic, String> param) {
                        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

                            final Button btn = new Button();

                            private void deleteTopic(OMTopic topicTemplate){
                                if(!App.user.userid.equals(topicTemplate.owner)){
                                    return;
                                }

                                Transaction.Created trc = null;
                                try {
                                    trc = TransactionFactory.create(App.mTransactionManager, 1000*5);
                                } catch (Exception e) {
                                    System.out.println("Could not create transaction " + e);;
                                }

                                Transaction txn = trc.transaction;


                                try{
                                    OMComment commentTemplate = new OMComment();
                                    OMPost postTemplate = new OMPost();
                                    OMTopicCounter counterTemplate = new OMTopicCounter();

                                    commentTemplate.topicId = topicTemplate.id;

                                    ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(commentTemplate)), txn, 1000, Long.MAX_VALUE);


                                    postTemplate.topicId = topicTemplate.id;

                                    ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(postTemplate)), txn, 1000, Long.MAX_VALUE);

                                    ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(topicTemplate)), txn, 1000, Long.MAX_VALUE);

                                    OMTopicCounter counter = (OMTopicCounter) App.mSpace.takeIfExists(counterTemplate, txn, 1000*4);

                                    counter.numOfTopics -= 1;

                                    App.mSpace.write(counter, txn, 1000*60*30);

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

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    System.out.println("In Table " + getTableView().getItems().get(getIndex()).owner + ", Logged In: " + App.user.userid);
                                    if(getTableView().getItems().get(getIndex()).owner.equals(App.user.userid)) {
                                        System.out.println("IN");
                                        btn.setOnAction(event -> deleteTopic(getTableView().getItems().get(getIndex())));
                                        ImageView imageView = new ImageView(new Image("resources/images/icons/rubbish-bin.png"));
                                        imageView.setFitHeight(18);
                                        imageView.setFitWidth(18);
                                        setMaxWidth(18);
                                        setAlignment(Pos.CENTER);
                                        btn.setGraphic(imageView);
                                        setGraphic(btn);
                                        btn.setBackground(Background.EMPTY);
                                        setText(null);
                                    } else {
                                        setGraphic(null);
                                        setText(null);
                                    }
                                }
                            }
                        };
                        return cell;
                    }
                };

        Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> notificationCellFactory =
                new Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>>() {
                    @Override
                    public TableCell call(final TableColumn<OMTopic, String> param) {
                        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

                            final Button btn = new Button();

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        System.out.println( getTableView().getItems().get(getIndex()).owner );
                                    });
                                    ImageView imageView = new ImageView(new Image("resources/images/icons/alarm.png"));
                                    imageView.setFitHeight(18);
                                    imageView.setFitWidth(18);
                                    setMaxWidth(18);
                                    setAlignment(Pos.CENTER);
                                    btn.setGraphic(imageView);
                                    setGraphic(btn);
                                    btn.setBackground(Background.EMPTY);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        viewColumn.setCellFactory(viewCellFactory);
        editColumn.setCellFactory(editCellFactory);
        deleteColumn.setCellFactory(deleteCellFactory);
        notificationColumn.setCellFactory(notificationCellFactory);

        textColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().title));
        items = topicTable.getItems();


        topicTable.getColumns().addAll(textColumn, viewColumn, editColumn, deleteColumn, notificationColumn);

//        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            viewTopic((String)newValue);
//        });
//
//        listView.setItems(items);
//
//        listView.setCellFactory(c -> new TopicListCell());

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
                    items.add(topic);
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


    private void listenForMessages(){
        // create the exporter
        Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                        new BasicILFactory(), false, true);

        try {
            // register this as a remote object
            // and get a reference to the 'stu'
            stub = (RemoteEventListener) myDefaultExporter.export(this);

            // add the listener
            OMTopicCounter template = new OMTopicCounter();
            App.mSpace.notify(template, null, stub, Lease.FOREVER, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notify(RemoteEvent ev) {
        OMTopic template = new OMTopic();
        Platform.runLater(() -> {

            try {
                MatchSet set = ((JavaSpace05)App.mSpace).contents(Collections.singletonList(template),null, 1000*4, Long.MAX_VALUE);

                if(set != null) {
                    OMTopic topic = (OMTopic) set.next();
                    ArrayList<OMTopic> topics = new ArrayList<>(items);

                    while (topic != null) {
                        if(!items.contains(topic)) {
                            items.add(topic);
                        } else {
                            topics.remove(topic);
                        }

                        topic = (OMTopic) set.next();
                    }

                    items.removeAll(topics);

                }

            } catch (TransactionException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (UnusableEntryException e) {
                e.printStackTrace();
            }
        });
    }


}
