package test.controller;

import main.application.App;
import main.application.HashPassword;
import main.controller.WelcomeController;
import main.model.OMUser;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SignedInUserTest extends JavaSpaceTest {
    public  SignedInUserTest() {
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
}
