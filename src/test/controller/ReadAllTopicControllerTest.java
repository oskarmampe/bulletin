package test.controller;

import main.application.App;
import main.application.HashPassword;
import main.callbacks.DeleteCallback;
import main.callbacks.NotificationCallback;
import main.controller.PopupCreateTopicController;
import main.controller.ReadAllTopicController;
import main.model.*;
import net.jini.space.JavaSpace05;
import net.jini.space.MatchSet;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class ReadAllTopicControllerTest extends SignedInUserTest{

    @Test
    public void getOneNotificationsTest(){
        OMNotification notification = new OMNotification();
        notification.delete = true;
        notification.comment = null;
        notification.topicName = "Test Topic";
        notification.topicId = "test";
        notification.index = 0;
        notification.userId = App.mUser.userid;

        try {
            App.mSpace.write(notification, null, 1000 * 2);

            ReadAllTopicController controller = new ReadAllTopicController();
            MatchSet set = controller.getNotifications();

            assertNotNull(set);
            OMNotification notificationFromSpace = (OMNotification) set.next();
            assertNotNull(notificationFromSpace);

            assertEquals(notificationFromSpace.topicName, notification.topicName);
            assertEquals(notificationFromSpace.userId, notification.userId);
            assertEquals(notificationFromSpace.topicId, notification.topicId);
            assertEquals(notificationFromSpace.index, notification.index);
            assertEquals(notificationFromSpace.comment, notification.comment);
            assertEquals(notificationFromSpace.delete, notification.delete);

            assertNull(set.next());
        } catch (Exception e) {
            fail();
        }

    }


    @Test
    public void logoutTest() {
        assertNotNull(App.mUser);

        OMLoggedInUser loggedInUser = new OMLoggedInUser();
        loggedInUser.userId = App.mUser.userid;
        try {
            assertNotNull(App.mSpace.read(loggedInUser, null, 1000 * 2));
            ReadAllTopicController controller = new ReadAllTopicController();
            controller.logout();
            assertNull(App.mSpace.take(loggedInUser, null, 1000*2));
        } catch (Exception e) {
            fail();
        }


    }

    @Test
    public void deleteTopicTest() {
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000*2));

            DeleteCallback callback = new DeleteCallback();
            callback.deleteTopic(template);
            assertNull(App.mSpace.read(template, null, 1000*2));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void deleteTopicNotCreatedByUserTest() {
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000*2));

            OMUser user = new OMUser();
            user.userid = "loggedinuser";
            user.salt = HashPassword.generateRandomSalt();
            user.password = HashPassword.convertToHash("loggedinuserpass", user.salt);
            assertNotNull(App.mSpace.write(user, null, 10000));
            App.mUser = user;

            DeleteCallback callback = new DeleteCallback();
            callback.deleteTopic(template);
            assertNotNull(App.mSpace.read(template, null, 1000*2));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void registerForNotificationsTest(){
        NotificationCallback callback = new NotificationCallback();
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000 * 4));
            callback.registerForNotifications(template);
            OMNotificationRegister register = new OMNotificationRegister();
            register.userId = App.mUser.userid;
            register.topicId = template.id;

            assertNotNull(App.mSpace.take(register, null, 1000*4));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void registerForNotificationsAlreadyExistingTest(){
        NotificationCallback callback = new NotificationCallback();
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000 * 2));
            callback.registerForNotifications(template);
            OMNotificationRegister register = new OMNotificationRegister();
            register.userId = App.mUser.userid;
            register.topicId = template.id;

            assertNotNull(App.mSpace.read(register, null, 1000*2));
            callback.registerForNotifications(template);

            Collection set = ((JavaSpace05)App.mSpace).take(Collections.singletonList(register), null, 1000, 2);
            assertNotNull(set);
            assertEquals(set.size(), 1);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void unregisterFromRegisterNotCreatedTest(){
        NotificationCallback callback = new NotificationCallback();
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000 * 2));
            callback.unregisterForNotifications(template);
            OMNotificationRegister register = new OMNotificationRegister();
            register.userId = App.mUser.userid;
            register.topicId = template.id;

            assertNull(App.mSpace.read(register, null, 1000*2));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void unregisterForNotificationsTest(){
        NotificationCallback callback = new NotificationCallback();
        PopupCreateTopicController controller = new PopupCreateTopicController();
        controller.createTopic("TopicName");

        OMTopic template = new OMTopic();
        template.owner = App.mUser.userid;
        template.id = App.mUser.userid + 0 + "TopicName";
        template.index = 0;
        template.title = "TopicName";
        try {
            assertNotNull(App.mSpace.read(template, null, 1000 * 2));
            callback.registerForNotifications(template);

            OMNotificationRegister register = new OMNotificationRegister();
            register.userId = App.mUser.userid;
            register.topicId = template.id;
            assertNotNull(App.mSpace.read(register, null, 1000*2));

            callback.unregisterForNotifications(template);

            assertNull(App.mSpace.read(register, null, 1000*2));
        } catch (Exception e){
            fail();
        }
    }

}