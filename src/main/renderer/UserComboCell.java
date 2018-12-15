package main.renderer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * Combo cell extends {@link ListCell}, for integration of image cells inside a combo box.
 *
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see ListCell
 */
public class UserComboCell extends ListCell<String> {
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        setText(null);
        if (item != null){
            ImageView imageView = new ImageView(new Image(item));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            setMaxWidth(50);
            setPadding(new Insets(5,0,5,0));
            setAlignment(Pos.CENTER);
            setGraphic(imageView);
        }
    }
}
