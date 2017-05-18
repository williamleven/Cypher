package com.github.cypher.gui.root.roomcollection.room.chat;


import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.settings.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Room;
import com.github.cypher.model.SdkException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChatPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@FXML
	private ListView eventListView;

	@FXML
	private TextArea messageBox;

	@FXML
	private Label roomName;

	@FXML
	private Label roomTopic;

	private final ResourceBundle bundle = ResourceBundle.getBundle(
		"com.github.cypher.gui.root.roomcollection.room.chat.chat",
		Locale.getDefault());


	@FXML
	private void initialize() {
		eventBus.register(this);
		messageBox.setDisable(client.getSelectedRoom() == null);
	}

	@Subscribe
	private void selectedRoomChanged(Room e){
		Platform.runLater(() -> {
			messageBox.setDisable(false);
			(new FXThreadedObservableValueWrapper<>(e.nameProperty())).addListener((invalidated) -> {
				updateRoomName(e);
			} );

			(new FXThreadedObservableValueWrapper<>(e.topicProperty())).addListener((invalidated) -> {
				updateTopicName(e);
			} );

			updateRoomName(e);
			updateTopicName(e);

		});
	}

	private void updateRoomName(Room e){
		if (e.getName() == null || e.getName().isEmpty()) {
			roomName.textProperty().setValue(bundle.getString("name.default"));
		}else{
			roomName.textProperty().setValue(e.getName());
		}
	}
	private void updateTopicName(Room e){
		if (e.getTopic() == null || e.getTopic().isEmpty()) {
			roomTopic.textProperty().setValue(bundle.getString("topic.default"));
		}else{
			roomTopic.textProperty().setValue(e.getTopic());
		}
	}

	@Subscribe
	private void handleLoginStateChange(ToggleEvent e) {
		if (e == ToggleEvent.LOGOUT) {
			messageBox.setDisable(true);
		}
	}

	@FXML
	private void showRoomSettings() {
		eventBus.post(ToggleEvent.SHOW_ROOM_SETTINGS);
	}

	@FXML
	private void onMessageBoxKeyPressed(KeyEvent event) {
		if(KeyCode.ENTER.equals(event.getCode())) {
			if (((settings.getControlEnterToSendMessage() && event.isControlDown())
			 || (!settings.getControlEnterToSendMessage() && !event.isShiftDown()))
			 &&  !messageBox.getText().isEmpty()) {

				Room room = client.getSelectedRoom();
				if(room != null) {
					try {
						room.sendMessage(messageBox.getText());
					} catch(SdkException e) {
						System.out.printf("SdkException when trying to send a message: %s\n", e);
					}
				}
				messageBox.clear();
				event.consume();

			} else if(event.isShiftDown()) {
				messageBox.insertText(
						messageBox.getCaretPosition(),
						"\n"
				);
			}
		}
	}
}
