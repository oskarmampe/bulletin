package callbacks;

import application.App;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import model.OMNotification;
import model.OMNotificationRegister;
import model.OMTopic;

public class NotificationCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {
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
                            try {
                                OMNotificationRegister template = new OMNotificationRegister();
                                template.userId = App.mUser.userid;
                                template.topicId = getTableView().getItems().get(getIndex()).id;

                                App.mSpace.take(template, null, 1000 * 60 * 30);

                                imageView.setImage(new Image("resources/images/icons/alarm.png"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        imageView.setImage(new Image("resources/images/icons/silence.png"));
                        setText(null);
                    } else {
                        btn.setOnAction(event -> {
                            try {
                                OMNotificationRegister template = new OMNotificationRegister();
                                template.userId = App.mUser.userid;
                                template.topicId = getTableView().getItems().get(getIndex()).id;

                                App.mSpace.write(template, null, 1000 * 60 * 30);

                                imageView.setImage(new Image("resources/images/icons/silence.png"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        imageView.setImage(new Image("resources/images/icons/alarm.png"));
                        setText(null);
                    }
                }
            }
        };
        return cell;
    }
}
