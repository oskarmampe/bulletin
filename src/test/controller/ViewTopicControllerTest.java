package test.controller;

import main.application.App;
import main.controller.PopupCreateTopicController;
import main.controller.ViewTopicController;
import main.model.OMComment;
import main.model.OMTopic;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ViewTopicControllerTest extends SignedInUserTest {

    public ViewTopicControllerTest() {
        super();
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000*5));
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void sendingPublicMessageTest() {
        ViewTopicController controller = new ViewTopicController();
        HashMap<String, OMTopic> map = new HashMap<>();
        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(template, null, 1000*2);
            assertNotNull(topic);
            map.put("topic", topic);
            controller.setMap(map);
            controller.sendMessage("message");
            OMComment comment = new OMComment();
            comment.owner = App.mUser.userid;
            comment.privateMessage = false;
            comment.topicId = topic.id;
            comment.content = "message";

            assertNotNull(App.mSpace.take(comment, null, 1000*3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void sendingPrivateMessageTest() {
        ViewTopicController controller = new ViewTopicController();
        HashMap<String, OMTopic> map = new HashMap<>();
        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(template, null, 1000*2);
            assertNotNull(topic);
            map.put("topic", topic);
            controller.setMap(map);
            controller.sendPrivateMessage("private message");
            OMComment comment = new OMComment();
            comment.owner = App.mUser.userid;
            comment.privateMessage = true;
            comment.topicId = topic.id;
            comment.content = "private message";
            comment.index = 0;

            assertNotNull(App.mSpace.take(comment, null, 1000*2));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void sendingEmptyPrivateMessage() {
        ViewTopicController controller = new ViewTopicController();
        HashMap<String, OMTopic> map = new HashMap<>();
        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(template, null, 1000*2);
            assertNotNull(topic);
            map.put("topic", topic);
            controller.setMap(map);
            controller.sendPrivateMessage("");
            OMComment comment = new OMComment();
            comment.owner = App.mUser.userid;
            comment.privateMessage = true;
            comment.topicId = topic.id;
            comment.content = "";
            comment.index = 0;

            assertNull(App.mSpace.take(comment, null, 1000*2));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void sendingEmptyPublicMessageTest() {
        ViewTopicController controller = new ViewTopicController();
        HashMap<String, OMTopic> map = new HashMap<>();
        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(template, null, 1000*2);
            assertNotNull(topic);
            map.put("topic", topic);
            controller.setMap(map);
            controller.sendMessage("");
            OMComment comment = new OMComment();
            comment.owner = App.mUser.userid;
            comment.privateMessage = false;
            comment.topicId = topic.id;
            comment.content = "";
            comment.index = 0;

            assertNull(App.mSpace.read(comment, null, 1000*2));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void sendingPrivateMessageWithoutTopicExisting(){
        ViewTopicController controller = new ViewTopicController();
        HashMap<String, OMTopic> map = new HashMap<>();
        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            OMTopic topic = (OMTopic) App.mSpace.take(template, null, 1000*2);
            assertNotNull(topic);
            map.put("topic", topic);
            controller.setMap(map);
            controller.sendPrivateMessage("private message");
            OMComment comment = new OMComment();
            comment.owner = App.mUser.userid;
            comment.privateMessage = true;
            comment.topicId = topic.id;
            comment.content = "private message";
            comment.index = 0;

            assertNull(App.mSpace.take(comment, null, 1000*2));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void sendingPublicMessageWithoutTopicExisting(){
        ViewTopicController controller = new ViewTopicController();
        HashMap<String, OMTopic> map = new HashMap<>();
        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            OMTopic topic = (OMTopic) App.mSpace.take(template, null, 1000*2);
            assertNotNull(topic);
            map.put("topic", topic);
            controller.setMap(map);
            controller.sendMessage("message");
            OMComment comment = new OMComment();
            comment.owner = App.mUser.userid;
            comment.privateMessage = false;
            comment.topicId = topic.id;
            comment.content = "message";

            assertNull(App.mSpace.take(comment, null, 1000*3));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}