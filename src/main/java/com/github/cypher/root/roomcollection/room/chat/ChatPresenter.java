package com.github.cypher.root.roomcollection.room.chat;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Room;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import java.io.IOException;

public class ChatPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private ListView eventList;

	@FXML
	private TextArea messageBox;

	@FXML
	private void initialize() {
		messageBox.setDisable(client.selectedRoom.getValue() == null);
		client.selectedRoom.addListener((observable, oldValue, newValue) -> {
			messageBox.setDisable(newValue == null);
		});
	}

	@FXML
	private void showRoomSettings() {
		client.showRoomSettings.set(true);
	}

	@FXML
	private void onMessageBoxKeyPressed(KeyEvent event) {
		if(event.isControlDown() &&
		   KeyCode.ENTER.equals(event.getCode()) &&
		   !messageBox.getText().equals("")) {

			Room room = client.selectedRoom.getValue();
			if(room != null) {
				try {
					room.sendMessage(messageBox.getText());
				} catch(RestfulHTTPException e) {
					if(DebugLogger.ENABLED) {
						DebugLogger.log("RestfulHTTPException when trying to send a message: " + e);
					}
				} catch(IOException e) {
					if(DebugLogger.ENABLED) {
						DebugLogger.log("IOException when trying to send a message: " + e);
					}
				}
			}
			messageBox.setText("");
		}
	}
}
