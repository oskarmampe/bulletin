package main.renderer;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;

public class TopicListCell extends TableCell<String, String> {

    public Button btn = new Button();

    public TopicListCell(EventHandler<MouseEvent> evt) {
        super();
        btn.setOnMouseClicked(evt);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null){
            ImageView imageView = new ImageView(new Image(item));
            imageView.setFitHeight(24);
            imageView.setFitWidth(24);
            setMaxWidth(24);
            setAlignment(Pos.CENTER);
            btn.setGraphic(imageView);
            setGraphic(btn);
            btn.setBackground(Background.EMPTY);
        }
    }
}
