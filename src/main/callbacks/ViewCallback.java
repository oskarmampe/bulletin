package main.callbacks;

import main.controller.SceneNavigator;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import main.model.OMTopic;

import java.util.HashMap;

/**
 *
 * Callback of a column. Responsible for cell rendering within a table. This one implements the view comments functionality.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Callback
 */
public class ViewCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {

    @Override
    public TableCell<OMTopic, String> call(final TableColumn<OMTopic, String> param) {
        return new TableCell<OMTopic, String>() {

            final Button mBtn = new Button();

            private void viewTopic(OMTopic value){
                HashMap<String, OMTopic> topicHashMap = new HashMap<>();
                topicHashMap.put("topic", value);
                SceneNavigator.loadScene(SceneNavigator.VIEW_TOPIC, topicHashMap);

            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    //------- CREATING IMAGE PROPERTIES -------
                    ImageView imageView = new ImageView(new Image("main/resources/images/icons/view.png"));
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);

                    //------- CREATING BUTTON PROPERTIES -------
                    mBtn.setOnAction(event -> {
                        viewTopic( getTableView().getItems().get(getIndex()) );
                    });
                    mBtn.setGraphic(imageView);
                    mBtn.setBackground(Background.EMPTY);

                    //------- CREATING CELL PROPERTIES -------
                    setGraphic(mBtn);
                    setMaxWidth(18);
                    setAlignment(Pos.CENTER);
                    setText(null);
                }
            }
        };
    }
}
