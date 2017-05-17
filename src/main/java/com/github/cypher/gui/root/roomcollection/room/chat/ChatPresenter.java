package com.github.cypher.gui.root.roomcollection.room.chat;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Room;
import com.github.cypher.model.SdkException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;

public class ChatPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private ListView eventListView;

	@FXML
	private TextArea messageBox;

	@FXML
	private void initialize() {
		messageBox.setDisable(client.selectedRoom.getValue() == null);
		client.selectedRoom.addListener((observable, oldValue, newValue) -> {
			messageBox.setDisable(newValue == null);
		});

		client.selectedRoom.addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> {
				//eventListView.setItems(newValue.);
			});
		});
	}

	@FXML
	private void showRoomSettings() {
		client.showRoomSettings.set(true);
	}

	@FXML
	private void onMessageBoxKeyPressed(KeyEvent event) {
		if(KeyCode.ENTER.equals(event.getCode())) {

			if (((settings.getControlEnterToSendMessage() && event.isControlDown())
			 || (!settings.getControlEnterToSendMessage() && !event.isShiftDown()))
			 &&  !messageBox.getText().isEmpty()) {

				Room room = client.selectedRoom.getValue();
				if(room != null) {
					try {
						room.sendMessage(messageBox.getText());
					} catch(SdkException e) {
						System.out.printf("SdkException when trying to send a message: %s\n", e);
					}
				}
				messageBox.clear();

			} else if(event.isShiftDown()) {
				messageBox.insertText(
						messageBox.getCaretPosition(),
						"\n"
				);
			}
		}
	}
}
