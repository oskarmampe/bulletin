package callbacks;

import application.App;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import model.OMComment;
import model.OMPost;
import model.OMTopic;
import model.OMTopicCounter;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.space.JavaSpace05;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;

public class DeleteCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {
    @Override
    public TableCell call(final TableColumn<OMTopic, String> param) {
        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

            final Button btn = new Button();

            private void deleteTopic(OMTopic topicTemplate){
                if(!App.mUser.userid.equals(topicTemplate.owner)){
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
                    System.out.println("In Table " + getTableView().getItems().get(getIndex()).owner + ", Logged In: " + App.mUser.userid);
                    if(getTableView().getItems().get(getIndex()).owner.equals(App.mUser.userid)) {
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
}
