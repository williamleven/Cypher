package com.github.cypher.gui.root.roomcollection.room.chat;

import com.github.cypher.DebugLogger;
import com.github.cypher.gui.FXThreadedObservableListWrapper;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.settings.Settings;
import com.github.cypher.gui.root.roomcollection.room.chat.eventlistitem.EventListItemPresenter;
import com.github.cypher.gui.root.roomcollection.room.chat.eventlistitem.EventListItemView;
import com.github.cypher.model.Client;
import com.github.cypher.model.Event;
import com.github.cypher.model.Room;
import com.github.cypher.model.SdkException;
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
	private ListView<Event> eventListView;

	@FXML
	private TextArea messageBox;

	@FXML
	private void initialize() {
		messageBox.setDisable(client.selectedRoom.getValue() == null);

		eventListView.setCellFactory(listView -> {
			EventListItemView view = new EventListItemView();
			return (EventListItemPresenter)view.getPresenter();
		});

		new FXThreadedObservableValueWrapper<>(client.selectedRoom).addListener((observable, oldValue, newValue) -> {
			messageBox.setDisable(newValue == null);
			if(newValue != null) {
				FXThreadedObservableListWrapper<Event> eventList =
						new FXThreadedObservableListWrapper<>(newValue.getEvents());

				eventListView.setItems(eventList.getList());
			} else {
				eventListView.setItems(null);
			}
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
