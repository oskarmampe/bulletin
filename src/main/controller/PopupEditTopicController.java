package main.controller;

import main.application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import main.model.OMTopic;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;

import java.util.HashMap;

/**
 *
 * PopupEditTopicController main.controller. This is injected using JavaFX from main.view/popup_edit_topic.fxml
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 */
public class PopupEditTopicController implements ParametrizedController<String, OMTopic> {

    private HashMap<String, OMTopic> mMap; //Map that is passed from SceneNavigator

    @FXML
    TextField topicNameTxt;


    /**
     *
     * Injected into JavaFX using popup_edit_topic.fxml.
     *
     */
    public void onEditButtonClick() {
        editTopic(topicNameTxt.getText());

        SceneNavigator.closePopupWindow();
    }

    /**
     *
     * Take the topic from {@link net.jini.space.JavaSpace}, edit it, and send it back to the space.
     * Transaction needed as, if the object is taken from the space, it needs to be written back in case of failure.
     *
     * @param topicName {@link String} the topic name to set the selected topic to
     */
    public void editTopic(String topicName) {
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
            OMTopic template = new OMTopic();
            template.id = mMap.get("topic").id;
            template.title = mMap.get("topic").title;
            template.owner = App.mUser.userid;

            try {
                OMTopic topic = (OMTopic) App.mSpace.take(template, txn, 1000);

                if(topic != null) {
                    topic.title = topicName;
                    App.mSpace.write(topic, txn, 1000 * 60 * 30);
                }
                txn.commit();
                //------- END OF TRANSACTION -------
            } catch (Exception e) {
                e.printStackTrace();
                txn.abort();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setParameters(HashMap<String, OMTopic> map) {
        mMap = map;
    }
}
