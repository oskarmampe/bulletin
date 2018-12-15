package main.callbacks;

import main.application.App;
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
 * Callback of a column. Responsible for cell rendering within a table. This one implements the edit topic functionality.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Callback
 */
public class EditCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {
    @Override
    public TableCell<OMTopic, String> call(final TableColumn<OMTopic, String> param) {
        return new TableCell<OMTopic, String>() {

            final Button mBtn = new Button();

            /**
             *
             * Displays the edit topic popup, and sends the topic to be edited in a {@link HashMap}
             *
             * @param topic {@link OMTopic}
             */
            private void editTopic(OMTopic topic) {
                HashMap<String, OMTopic> map = new HashMap<>();
                map.put("topic", topic);
                SceneNavigator.showPopupWindow(SceneNavigator.EDIT_TOPIC_POPUP, map);
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if(getTableView().getItems().get(getIndex()).owner.equals(App.mUser.userid)) {
                        //------- SETTING UP EDIT IMAGE -------
                        ImageView imageView = new ImageView(new Image("main/resources/images/icons/edit.png"));
                        imageView.setFitHeight(18);
                        imageView.setFitWidth(18);

                        //------- SETTING BUTTON -------
                        mBtn.setOnAction(event -> {
                            editTopic(getTableView().getItems().get(getIndex()));
                        });
                        mBtn.setGraphic(imageView);
                        mBtn.setBackground(Background.EMPTY);

                        //------- SETTING CELL PROPERTIES -------
                        setGraphic(mBtn);
                        setAlignment(Pos.CENTER);
                        setMaxWidth(18);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(null);
                    }
                }
            }
        };
    }
}
