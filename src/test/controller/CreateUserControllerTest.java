package test.controller;

import com.sun.tools.javac.util.List;
import main.application.App;
import main.application.HashPassword;
import main.controller.CreateUserController;
import main.model.OMUser;
import net.jini.core.entry.Entry;
import net.jini.space.JavaSpace05;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class CreateUserControllerTest extends JavaSpaceTest {


    @Test
    public void emptyPasswordTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("test user","", "main/resources/images/icons/boy.png");

        OMUser user  = new OMUser();
        user.userid = "test user";
        user.image = "main/resources/images/icons/boy.png";
        try {
            assertNull(App.mSpace.take(user, null, 1000 * 3));

        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void emptyUsernameTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("","test pass", "main/resources/images/icons/boy.png");

        OMUser user  = new OMUser();
        user.userid = "";
        user.image = "main/resources/images/icons/boy.png";
        try {
            assertNull(App.mSpace.take(user, null, 1000 * 3));

        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void emptyDetailsTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("","", "main/resources/images/icons/boy.png");

        OMUser user  = new OMUser();
        user.userid = "";
        user.image = "main/resources/images/icons/boy.png";
        try {
            assertNull(App.mSpace.take(user, null, 1000 * 3));

        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void takenDetailsTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("taken username","password", "main/resources/images/icons/boy.png");
        controller.createUser("taken username","different password", "main/resources/images/icons/man.png");
        OMUser firstUser  = new OMUser();
        firstUser.userid = "taken username";
        firstUser.image = "main/resources/images/icons/boy.png";

        OMUser secondUser = new OMUser();
        secondUser.userid = "taken username";
        secondUser.image = "main/resources/images/icons/man.png";

        try {
            Collection set = ((JavaSpace05)App.mSpace).take(new ArrayList<Entry>(List.of(firstUser, secondUser)),
                    null, 1000 * 3, 2);
            assertEquals(set.size(), 1);

        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void createUserTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("good username","brilliant password", "main/resources/images/icons/boy.png");
        OMUser user  = new OMUser();
        user.userid = "good username";
        user.image = "main/resources/images/icons/boy.png";

        try {
            OMUser createdUser = (OMUser) App.mSpace.take(user, null, 1000 * 3);
            assertNotNull(createdUser);
            assertNotNull(createdUser.salt);
            assertEquals(createdUser.password, HashPassword.convertToHash("brilliant password", createdUser.salt));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void hasPasswordBeenHashedTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("username","hashedpassword", "main/resources/images/icons/boy.png");

        OMUser user  = new OMUser();
        user.userid = "username";
        user.image = "main/resources/images/icons/boy.png";
        user.password = "hashedpassword";
        try {
            assertNull(App.mSpace.take(user, null, 1000 * 3));

        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void hasPasswordBeenHashedWithSaltTest() {
        CreateUserController controller = new CreateUserController();
        controller.createUser("username","hashedpassword", "main/resources/images/icons/boy.png");

        OMUser user  = new OMUser();
        user.userid = "username";
        user.image = "main/resources/images/icons/boy.png";
        user.password = HashPassword.convertToHash("hashedpassword", HashPassword.generateRandomSalt());
        try {
            assertNull(App.mSpace.take(user, null, 1000 * 3));

        } catch (Exception e){
            fail();
        }
    }

}