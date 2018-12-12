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

public class EditCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {
    @Override
    public TableCell call(final TableColumn<OMTopic, String> param) {
        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

            final Button btn = new Button();

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
                        btn.setOnAction(event -> {
                            editTopic(getTableView().getItems().get(getIndex()));
                        });
                        ImageView imageView = new ImageView(new Image("main/resources/images/icons/edit.png"));
                        imageView.setFitHeight(18);
                        imageView.setFitWidth(18);
                        setMaxWidth(18);
                        setAlignment(Pos.CENTER);
                        btn.setGraphic(imageView);
                        setGraphic(btn);
                        btn.setBackground(Background.EMPTY);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(null);
                    }
                }
            }
        };
        return cell;
    }
}
