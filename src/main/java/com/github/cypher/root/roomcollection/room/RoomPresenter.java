package com.github.cypher.root.roomcollection.room;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class RoomPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	AnchorPane settingsPane;

	@FXML
	SplitPane chatRootPane;

	@FXML
	private void initialize() {
		client.selectedRoom.addListener(((observable, oldValue, newValue) -> {
			// If a new room is selected the RoomSettings view is hidden.
			if (oldValue != null && newValue != null && oldValue != newValue) {
				client.showRoomSettings.set(false);
			}
		}));

		client.showRoomSettings.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				settingsPane.toFront();
			} else {
				chatRootPane.toFront();
			}
		});
	}
}
