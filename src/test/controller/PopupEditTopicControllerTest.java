package test.controller;

import main.application.App;
import main.application.HashPassword;
import main.controller.PopupCreateTopicController;
import main.controller.PopupEditTopicController;
import main.model.OMTopic;
import main.model.OMUser;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class PopupEditTopicControllerTest extends SignedInUserTest{


    public PopupEditTopicControllerTest() {
        super();
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000*10));
        } catch (Exception e) {
            fail();
        }
    }



    @Test
    public void editExistingTopicTest() {
        PopupEditTopicController controller = new PopupEditTopicController();
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(
                    new OMTopic(App.mUser.userid + 0 + "TopicName", 0, App.mUser.userid, "TopicName"),
                    null, 1000);

            assertNotNull(topic);
            HashMap<String, OMTopic> map = new HashMap<>();
            map.put("topic", topic);
            controller.setParameters(map);

            controller.editTopic("Edited Topic");

            assertNotNull(App.mSpace.take(
                    new OMTopic(App.mUser.userid + 0 + "TopicName", 0, App.mUser.userid, "Edited Topic"),
                    null, 1000));
        } catch (Exception e){
            fail();
        }

    }

    @Test
    public void editUserHasNotCreatedTest() {
        PopupEditTopicController controller = new PopupEditTopicController();
        try {
            OMTopic topic = (OMTopic) App.mSpace.read(
                    new OMTopic(App.mUser.userid + 0 + "TopicName", 0, App.mUser.userid, "TopicName"),
                    null, 1000);

            assertNotNull(topic);
            HashMap<String, OMTopic> map = new HashMap<>();
            map.put("topic", topic);
            controller.setParameters(map);

            OMUser user = new OMUser();
            user.userid = "loggedinuser";
            user.salt = HashPassword.generateRandomSalt();
            user.password = HashPassword.convertToHash("loggedinuserpass", user.salt);
            assertNotNull(App.mSpace.write(user, null, 10000));
            App.mUser = user;

            controller.editTopic("Edited Topic Without Creating It");

            assertNull(App.mSpace.take(
                    new OMTopic(App.mUser.userid + 0 + "TopicName", 0, App.mUser.userid, "Edited Topic Without Creating It"),
                    null, 1000));
        } catch (Exception e){
            fail();
        }
    }

}