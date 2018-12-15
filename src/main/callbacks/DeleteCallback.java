package main.callbacks;

import main.application.App;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import main.model.*;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * Callback of a column. Responsible for cell rendering within a table. This one implements the delete topic functionality.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Callback
 */
public class DeleteCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {

    /**
     *
     *
     *
     * @param topicTemplate {@link OMTopic} to be deleted from {@link net.jini.space.JavaSpace}
     *                                     and table {@link main.controller.ReadAllTopicController#topicTable}
     */
    public void deleteTopic(OMTopic topicTemplate){
        if(!App.mUser.userid.equals(topicTemplate.owner)){
            return;
        }
        //------- TRANSACTION -------
        Transaction.Created trc = null;
        try {
            trc = TransactionFactory.create(App.mTransactionManager, 1000*5);
        } catch (Exception e) {
            System.out.println("Could not create transaction " + e);
        }

        Transaction txn = trc.transaction;


        try{
            //------- TEMPLATES -------
            OMComment commentTemplate = new OMComment();
            OMTopicCounter counterTemplate = new OMTopicCounter();
            OMNotification notificationTemplate = new OMNotification();

            notificationTemplate.topicId = topicTemplate.id;
            notificationTemplate.topicName = topicTemplate.title;
            notificationTemplate.delete = false;

            commentTemplate.topicId = topicTemplate.id;

            //------- TAKE ANY ITEMS FROM SPACE -------
            ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(commentTemplate)), txn, 1000, Long.MAX_VALUE);
            ((JavaSpace05)App.mSpace).take(new ArrayList<>(Collections.singletonList(topicTemplate)), txn, 1000, Long.MAX_VALUE);
            OMTopicCounter counter = (OMTopicCounter) App.mSpace.takeIfExists(counterTemplate, txn, 1000*4);

            counter.numOfTopics -= 1;

            App.mSpace.write(counter, txn, 1000*60*30);//write the counter to space after changing the topic value

            OMNotificationRegister registerTemplate = new OMNotificationRegister();
            registerTemplate.topicId = topicTemplate.id;

            //------- SIGNALING USERS ABOUT DELETION OF TOPIC -------
            MatchSet set = ((JavaSpace05) App.mSpace).contents(Collections.singletonList(registerTemplate), txn,
                    1000 * 5, Long.MAX_VALUE);

            //---------- NOTIFICATIONS ------------
            if (set != null) {
                OMNotificationRegister register = (OMNotificationRegister) set.next();
                ArrayList<OMNotification> notifications = new ArrayList<>();

                while (register != null) {
                    if (!register.userId.equals(App.mUser.userid)) {

                        OMNotification notification = new OMNotification();
                        notification.userId = register.userId;
                        notification.topicName = topicTemplate.title;
                        notification.delete = true;
                        notification.topicId = topicTemplate.id;

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

            txn.commit();
            //------- END OF TRANSACTION -------
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
    public TableCell<OMTopic, String> call(final TableColumn<OMTopic, String> param) {
        return new TableCell<OMTopic, String>() {

            final Button mBtn = new Button();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if(getTableView().getItems().get(getIndex()).owner.equals(App.mUser.userid)) {
                        //------- SETTING UP DELETE ICON -------
                        ImageView imageView = new ImageView(new Image("main/resources/images/icons/rubbish-bin.png"));
                        imageView.setFitHeight(18);
                        imageView.setFitWidth(18);

                        //------- SETTING UP BUTTON -------
                        mBtn.setOnAction(event -> deleteTopic(getTableView().getItems().get(getIndex())));
                        mBtn.setGraphic(imageView);
                        mBtn.setBackground(Background.EMPTY);

                        //------- SETTING CELL PROPERTIES -------
                        setMaxWidth(18);
                        setAlignment(Pos.CENTER);
                        setGraphic(mBtn);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(null);
                    }
                }
            }
        };
    }
}
