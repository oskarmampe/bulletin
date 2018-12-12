package main.controller;

import main.application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import main.model.OMTopic;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;

import java.util.HashMap;

public class PopupEditTopicController implements ParametrizedController<String, OMTopic> {

    private HashMap<String, OMTopic> mMap;

    @FXML
    TextField topicNameTxt;


    public void onEditButtonClick() {
        editTopic(topicNameTxt.getText());

        SceneNavigator.closePopupWindow();
    }

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
