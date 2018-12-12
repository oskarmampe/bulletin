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

public class ViewCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {

    @Override
    public TableCell call(final TableColumn<OMTopic, String> param) {
        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

            final Button btn = new Button();

            public void viewTopic(OMTopic value){
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
                    btn.setOnAction(event -> {
                        viewTopic( getTableView().getItems().get(getIndex()) );
                    });
                    ImageView imageView = new ImageView(new Image("main/resources/images/icons/view.png"));
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);
                    setMaxWidth(18);
                    setAlignment(Pos.CENTER);
                    btn.setGraphic(imageView);
                    setGraphic(btn);
                    btn.setBackground(Background.EMPTY);
                    setText(null);
                }
            }
        };
        return cell;
    }
}
