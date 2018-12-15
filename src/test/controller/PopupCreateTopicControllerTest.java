package test.controller;

import com.sun.tools.javac.util.List;
import main.application.App;
import main.controller.PopupCreateTopicController;
import main.model.OMTopic;
import net.jini.core.entry.Entry;
import net.jini.space.JavaSpace05;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class PopupCreateTopicControllerTest extends SignedInUserTest {

    @Test
    public void createTopicTest() {
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.take(template, null, 1000*2));
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void emptyTitleTest() {
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "";
        template.index = 0;
        template.title = "";
        try {
            assertNull(App.mSpace.take(template, null, 1000*2));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void takenTitleTest() {
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");
        controller.createTopic("TopicName");

        OMTopic firstTemplate = new OMTopic();
        firstTemplate.owner = App.mUser.userid;
        firstTemplate.id = App.mUser.userid + 0 + "TopicName";
        firstTemplate.index = 0;
        firstTemplate.title = "TopicName";

        OMTopic secondTemplate = new OMTopic();
        secondTemplate.owner = App.mUser.userid;
        secondTemplate.id = App.mUser.userid + 1 + "TopicName";
        secondTemplate.index = 1;
        secondTemplate.title = "TopicName";
        try {
            Collection set = ((JavaSpace05)App.mSpace).take(new ArrayList<Entry>(List.of(firstTemplate, secondTemplate)), null, 1000*2, Long.MAX_VALUE);
            assertEquals(set.size(), 1);
        } catch (Exception e) {
            fail();
        }
    }

}