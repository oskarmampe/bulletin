package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMNotificationRegister;
import model.OMTopic;
import model.OMTopicCounter;
import model.OMUser;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.util.ArrayList;
import java.util.Collections;

public class PopupCreateTopicController {

    @FXML
    TextField topicNameTxt;

    public void createTopic() {

        OMTopic template = new OMTopic();
        try {
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
                Integer count = 0;
                OMTopicCounter counterTemplate = new OMTopicCounter();


                OMTopicCounter counter = (OMTopicCounter) App.mSpace.take(counterTemplate, txn, 1000);

                if (counter == null) {
                    MatchSet set = ((JavaSpace05) App.mSpace).contents(Collections.singletonList(template), txn, 1000, Long.MAX_VALUE);
                    if (set != null) {
                        count -= 1;
                        OMTopic topic = (OMTopic) set.next();

                        while (topic != null) {
                            count += 1;
                            topic = (OMTopic) set.next();
                        }
                    }
                    counter = new OMTopicCounter(count);
                }

                OMTopic obj = new OMTopic(App.mUser.userid + counter.numOfTopics + topicNameTxt.getText(),
                        counter.numOfTopics, App.mUser.userid, topicNameTxt.getText());

                counter.numOfTopics += 1;

                App.mSpace.write(obj, txn, 1000 * 60 * 20);
                App.mSpace.write(counter, txn, 1000 * 60 * 30);

                txn.commit();

                App.mLease.renew(1000*60*2);
            } catch (Exception e) {
                e.printStackTrace();
                txn.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SceneNavigator.closePopupWindow();
    }
}
