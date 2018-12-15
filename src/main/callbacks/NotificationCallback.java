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

/**
 *
 * Callback of a column. Responsible for cell rendering within a table. This one implements the notification topic functionality.
 * Author: Oskar Mampe: U1564420
 * Date: 10/11/2018
 *
 * @see Callback
 */
public class NotificationCallback implements Callback<TableColumn<OMTopic, String>, TableCell<OMTopic, String>> {

    /**
     *
     * Register for notifications about a specific {@link OMTopic}.
     *
     * @param topic {@link OMTopic} to receive notifications about
     */
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

    /**
     *
     * Unregister the user from notification for the particular {@link OMTopic}
     *
     * @param topic {@link OMTopic} to stop receiving notifications from
     */
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
    public TableCell<OMTopic, String> call(final TableColumn<OMTopic, String> param) {
        return new TableCell<OMTopic, String>() {

            final Button mBtn = new Button();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    //------- CHECKING FOR REGISTER -------
                    OMNotificationRegister register = null;
                    try {
                        OMNotificationRegister template = new OMNotificationRegister();
                        template.userId = App.mUser.userid;
                        template.topicId = getTableView().getItems().get(getIndex()).id;
                        register = (OMNotificationRegister) App.mSpace.read(template, null, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //------- SETTING IMAGE PROPERTIES -------
                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(18);
                    imageView.setFitWidth(18);

                    //------- SETTING BUTTON PROPERTIES -------
                    mBtn.setGraphic(imageView);
                    mBtn.setBackground(Background.EMPTY);
                    if(register != null) {
                        mBtn.setOnAction(event -> {
                            unregisterForNotifications(getTableView().getItems().get(getIndex()));
                            imageView.setImage(new Image("main/resources/images/icons/alarm.png"));
                        });
                        imageView.setImage(new Image("main/resources/images/icons/silence.png"));
                        setText(null);
                    } else {
                        mBtn.setOnAction(event -> {
                            registerForNotifications(getTableView().getItems().get(getIndex()));
                            imageView.setImage(new Image("main/resources/images/icons/silence.png"));
                        });
                        imageView.setImage(new Image("main/resources/images/icons/alarm.png"));
                        setText(null);
                    }

                    //------- SETTING CELL PROPERTIES -------
                    setMaxWidth(18);
                    setAlignment(Pos.CENTER);
                    setGraphic(mBtn);

                }
            }
        };
    }
}
