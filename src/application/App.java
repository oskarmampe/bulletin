package application;

import controller.MainController;
import controller.SceneNavigator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.SpaceUtils;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;

import java.io.IOException;

public class App extends Application {

    public static JavaSpace mSpace;
    public static TransactionManager mTransactionManager;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //SETTING STAGE
        primaryStage.setTitle("Assignment");

        SceneNavigator.setMainController(new MainController(primaryStage));
        SceneNavigator.loadScene(SceneNavigator.MAIN);

        //Needed to close properly, without this, the application is still running in the background
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();
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
