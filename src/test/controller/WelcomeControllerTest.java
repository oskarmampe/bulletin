package test.controller;

import main.application.App;
import main.application.HashPassword;
import main.controller.WelcomeController;
import main.model.OMUser;
import main.model.SpaceUtils;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.*;

public class WelcomeControllerTest extends JavaSpaceTest {

    @Test
    public void wrongRegistrationDetails() {
        WelcomeController controller = new WelcomeController();
        try {
            assertFalse(controller.getUserFromSpace("badusername", "badpassword"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void correctRegistrationDetails() {
        OMUser user = new OMUser();
        WelcomeController controller = new WelcomeController();

        try {
            user.userid = "goodusername";
            user.salt = HashPassword.generateRandomSalt();
            user.password = HashPassword.convertToHash("goodpassword", user.salt);
            App.mSpace.write(user, null, 10000);
            assertTrue(controller.getUserFromSpace("goodusername", "goodpassword"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void currentlyLoggedInUser() {
        OMUser user = new OMUser();
        WelcomeController controller = new WelcomeController();

        try {
            user.userid = "loggedinuser";
            user.salt = HashPassword.generateRandomSalt();
            user.password = HashPassword.convertToHash("loggedinuserpass", user.salt);
            App.mSpace.write(user, null, 10000);
            assertTrue(controller.getUserFromSpace("loggedinuser", "loggedinuserpass"));
            assertFalse(controller.getUserFromSpace("loggedinuser", "loggedinuserpass"));
        } catch (Exception e) {
            fail();
        }
    }
}