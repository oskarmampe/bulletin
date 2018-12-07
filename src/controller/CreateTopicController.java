package controller;

import application.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import model.OMTopic;
import model.OMTopicCounter;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;

import java.rmi.RemoteException;
import java.util.Collections;

public class CreateTopicController {
    @FXML
    TextField objTextField;


    public void createTopic() {

        OMTopic template = new OMTopic();

        try {
            OMTopicCounter counterTemplate = new OMTopicCounter();
            Integer count = 0;
            OMTopicCounter counter = (OMTopicCounter) App.mSpace.take(counterTemplate, null, 1000*10);

            if(counter == null){
                MatchSet set = ((JavaSpace05)App.mSpace).contents(Collections.singletonList(template), null, 1000*2, Long.MAX_VALUE);
                if(set != null) {
                    count -= 1;
                    OMTopic topic = (OMTopic) set.next();

                    while (topic != null) {
                        count += 1;
                        topic = (OMTopic) set.next();
                    }
                }
                counter = new OMTopicCounter(count);
            }

            OMTopic obj = new OMTopic(App.user.userid+counter.numOfTopics+objTextField.getText(), counter.numOfTopics, App.user.userid, objTextField.getText());

            counter.numOfTopics += 1;

            App.mSpace.write(obj, null, 1000 * 60 * 20);
            App.mSpace.write(counter, null, 1000 * 60 * 30);
        } catch (TransactionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (UnusableEntryException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void changeScene(){
        SceneNavigator.loadScene(SceneNavigator.READ_ALL_TOPICS);
    }
}
