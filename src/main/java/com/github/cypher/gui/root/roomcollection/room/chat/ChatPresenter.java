package com.github.cypher.gui.root.roomcollection.room.chat;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.gui.root.roomcollection.room.chat.messageitem.MessageItemPresenter;
import com.github.cypher.gui.root.roomcollection.room.chat.messageitem.MessageItemView;
import com.github.cypher.gui.root.roomcollection.roomlistitem.RoomListItemPresenter;
import com.github.cypher.model.*;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

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

		eventListView.setCellFactory(new Callback<ListView, ListCell>() {

			class MyListCell extends ListCell {
				@Override
				protected void updateItem(Object item, boolean empty) {

					super.updateItem(item, empty);
				}
			}

			@Override
			public ListCell call(ListView param) {
				return new MyListCell();
			}
		});
			Platform.runLater(() -> eventListView.setItems(client.selectedRoom.getValue().getEvents()));



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
						if(DebugLogger.ENABLED) {
							DebugLogger.log("SdkException when trying to send a message: " + e);
						}
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
