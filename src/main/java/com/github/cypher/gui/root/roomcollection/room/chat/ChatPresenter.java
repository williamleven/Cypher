package com.github.cypher.gui.root.roomcollection.room.chat;

import com.github.cypher.gui.FXThreadedObservableListWrapper;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.settings.Settings;
import com.github.cypher.gui.root.roomcollection.room.chat.eventlistitem.EventListItemPresenter;
import com.github.cypher.gui.root.roomcollection.room.chat.eventlistitem.EventListItemView;
import com.github.cypher.model.Client;
import com.github.cypher.model.Event;
import com.github.cypher.model.Room;
import com.github.cypher.model.SdkException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
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

	@Inject
	private EventBus eventBus;

	@FXML
	private ListView<Event> eventListView;

	@FXML
	private TextArea messageBox;

	private FXThreadedObservableListWrapper<Event> backendListForEventView;


	@FXML
	private void initialize() {
		eventBus.register(this);
		messageBox.setDisable(client.getSelectedRoom() == null);
	}

	@Subscribe
	private void selectedRoomChanged(Room room){
		Platform.runLater(() -> {
			messageBox.setDisable(room == null);

			if (backendListForEventView != null) {
				backendListForEventView.dispose();
			}
			backendListForEventView = new FXThreadedObservableListWrapper<>(room.getEvents());

			eventListView.setCellFactory(listView -> {
				EventListItemView memberListItemView = new EventListItemView();
				memberListItemView.getView();
				return (EventListItemPresenter)memberListItemView.getPresenter();
			});

			eventListView.setItems(backendListForEventView.getList());
		});
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

			} else if(event.isShiftDown()) {
				messageBox.insertText(
						messageBox.getCaretPosition(),
						"\n"
				);
			}
		}
	}
}
