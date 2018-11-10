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

public class App extends Application {

    public static JavaSpace mSpace;
    public static TransactionManager mTransactionManager;
    public static OMUser user;

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
    }

    /**
     * Loads the main fxml layout.
     * Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = (Pane) loader.load(getClass().getResourceAsStream(SceneNavigator.MAIN));

        MainController mainController = loader.getController();

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
        launch(args);
    }
}
