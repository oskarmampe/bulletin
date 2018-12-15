package main.controller;

import main.application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import main.model.OMTopic;
import main.model.OMTopicCounter;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.util.Collections;

/**
 *
 * PopupCreateTopicController main.controller. This is injected using JavaFX from main.view/popup_create_topic.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class PopupCreateTopicController {

    @FXML
    TextField topicNameTxt;

    /**
     *
     * Injected into JavaFX using popup_create_topic.fxml.
     *
     */
    public void onCreateTopicButtonClick() {
        createTopic(topicNameTxt.getText());

        SceneNavigator.closePopupWindow();
    }

    /**
     *
     * Create topic and write it into {@link net.jini.space.JavaSpace}
     *
     * @param topicName {@link OMTopic} to be added to the space
     */
    public void createTopic(String topicName) {
        if(topicName.isEmpty() || topicName.trim().equals(""))
            return;

        OMTopic template = new OMTopic();
        try {
            //------- TRANSACTION -------
            Transaction.Created trc = null;
            try {
                trc = TransactionFactory.create(App.mTransactionManager, 1000*10);
            } catch (Exception e) {
                System.out.println("Could not create transaction " + e);
            }

            //------- BEG OF TRANSACTION -------
            Transaction txn = trc.transaction;

            try {
                int count = 0;
                OMTopicCounter counterTemplate = new OMTopicCounter();

                //------- ADD TO THE COUNTER -------
                OMTopicCounter counter = (OMTopicCounter) App.mSpace.take(counterTemplate, txn, 1000);
                if (counter == null) {
                    MatchSet set = ((JavaSpace05) App.mSpace).contents(Collections.singletonList(template), txn, 1000, Long.MAX_VALUE);
                    if (set != null) {
                        OMTopic topic = (OMTopic) set.next();

                        while (topic != null) {
                            count += 1;
                            topic = (OMTopic) set.next();
                        }
                    }
                    counter = new OMTopicCounter(count);
                }


                //------- CREATE THE TOPIC -------
                OMTopic topicTemplate = new OMTopic();
                topicTemplate.title = topicName;

                OMTopic foundTopic = (OMTopic) App.mSpace.read(topicTemplate, null, 1000*2);
                if (foundTopic != null)
                    txn.abort();

                OMTopic obj = new OMTopic(App.mUser.userid + counter.numOfTopics + topicName,
                        counter.numOfTopics, App.mUser.userid, topicName);



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
    }
}
