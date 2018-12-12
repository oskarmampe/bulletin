package main.callbacks;

import main.application.App;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import main.model.OMNotificationRegister;
import main.model.OMTopic;

public class NotificationCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {

    public void registerForNotifications(OMTopic topic){
        try {
            OMNotificationRegister template = new OMNotificationRegister();
            template.userId = App.mUser.userid;
            template.topicId = topic.id;

            App.mSpace.write(template, null, 1000 * 60 * 30);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterForNotifications(OMTopic topic){
        try {
            OMNotificationRegister template = new OMNotificationRegister();
            template.userId = App.mUser.userid;
            template.topicId = topic.id;

            App.mSpace.take(template, null, 1000 * 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TableCell call(final TableColumn<OMTopic, String> param) {
        final TableCell<OMTopic, String> cell = new TableCell<OMTopic, String>() {

            final Button btn = new Button();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    OMNotificationRegister register = null;
                    try {
                        OMNotificationRegister template = new OMNotificationRegister();
                        template.userId = App.mUser.userid;
                        template.topicId = getTableView().getItems().get(getIndex()).id;
                        register = (OMNotificationRegister) App.mSpace.read(template, null, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);
                    setMaxWidth(18);
                    setAlignment(Pos.CENTER);
                    btn.setGraphic(imageView);
                    setGraphic(btn);
                    btn.setBackground(Background.EMPTY);

                    if(register != null) {
                        btn.setOnAction(event -> {
                            unregisterForNotifications(getTableView().getItems().get(getIndex()));
                            imageView.setImage(new Image("main/resources/images/icons/alarm.png"));
                        });
                        imageView.setImage(new Image("main/resources/images/icons/silence.png"));
                        setText(null);
                    } else {
                        btn.setOnAction(event -> {
                            registerForNotifications(getTableView().getItems().get(getIndex()));
                            imageView.setImage(new Image("main/resources/images/icons/silence.png"));
                        });
                        imageView.setImage(new Image("main/resources/images/icons/alarm.png"));
                        setText(null);
                    }
                }
            }
        };
        return cell;
    }
}
