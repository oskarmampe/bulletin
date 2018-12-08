package application;

import controller.MainController;
import controller.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.OMUser;
import model.SpaceUtils;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.IOException;


/**
 *
 * Main Application Class. Used in JavaFX as entry point. Contains basic methods for loading and creatin scenes.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Application
 */
public class App extends Application {

    //Javaspace entries. Set as static so they're loaded once, releasing pressure on network traffic.
    public static JavaSpace mSpace;
    public static TransactionManager mTransactionManager;

    //Logged in User.
    public static OMUser mUser;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //SETTING STAGE
        primaryStage.setTitle("Assignment");

        primaryStage.setScene(createScene(loadMainPane()));

        //Needed to close properly, without this, the application is still running in the background
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
        SceneNavigator.mPrimaryStage = primaryStage;
    }

    /**
     * Loads the main fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane mainPane = loader.load(getClass().getResourceAsStream(SceneNavigator.MAIN));

        //Main controller is the controller for the main fxml.
        MainController mainController = loader.getController();

        //Scenenavigator is a singleton responsible for scene switching. It needs the main controller to load the main fxml properly.
        SceneNavigator.setMainController(mainController);
        SceneNavigator.loadScene(SceneNavigator.WELCOME);

        return mainPane;
    }

    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     *
     * @return the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);

        scene.getStylesheets().setAll(getClass().getResource("../resources/styles/styles.css").toExternalForm());

        return scene;
    }


    /**
     *
     * Main method. Creates a {@link JavaSpace} and {@link TransactionManager} from {@link SpaceUtils}
     * Also launches fxml using any external args
     * @see Application
     *
     * @param args compiler arguments
     */
    public static void main(String[] args) {
        mSpace = SpaceUtils.getSpace();
        if (mSpace == null){
            System.err.println("Failed to find the javaspace");
            System.exit(1);
        }
        mTransactionManager = SpaceUtils.getManager();
        if (mTransactionManager == null){
            System.err.println("Failed to find the transaction manager");
            System.exit(1);
        }
        launch(args);//FXML call
    }
}
