package test.controller;

import com.sun.tools.javac.util.List;
import main.application.App;
import main.model.*;
import net.jini.core.entry.Entry;
import net.jini.space.JavaSpace05;

import java.util.ArrayList;

import static org.junit.Assert.fail;

public class JavaSpaceTest {


    public JavaSpaceTest() {
        App.mSpace = SpaceUtils.getSpace();
        if (App.mSpace == null){
            fail();
        }
        App.mTransactionManager = SpaceUtils.getManager();
        if (App.mTransactionManager == null){
            fail();
        }

        OMComment comment = new OMComment();
        OMLoggedInUser loggedInUser = new OMLoggedInUser();
        OMNotification notification = new OMNotification();
        OMNotificationRegister notificationRegister = new OMNotificationRegister();
        OMTopic topic = new OMTopic();
        OMTopicCounter topicCounter = new OMTopicCounter();
        OMUser user = new OMUser();

        ArrayList<Entry> entries = new ArrayList<Entry>(List.of(comment, loggedInUser, notification,
                notificationRegister, topic, topicCounter, user));

        try {
            ((JavaSpace05)App.mSpace).take(entries, null, 1000*2, Long.MAX_VALUE);
        } catch (Exception e) {
            fail();
        }
    }
}
