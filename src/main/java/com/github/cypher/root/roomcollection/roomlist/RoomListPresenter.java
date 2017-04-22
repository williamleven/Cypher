package com.github.cypher.root.roomcollection.roomlist;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class RoomListPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private void initialize() {
		client.selectedRoomCollection.addListener((observable, oldValue, newValue) -> {
			roomCollectionChanged(newValue);
		});

		client.selectedRoom.addListener(((observable, oldValue, newValue) -> {
			// TODO
		}));
	}

	// In the future if separate fxml/views/presenters exists for Server/PMCollection/GeneralCollection
	// we change which pane is in the front of the StackPane.
	//
	// Currently while only RoomCollection exists buttons are just enabled and disabled depending on which kind of
	// RoomCollection is currently shown.
	private void roomCollectionChanged(RoomCollection roomCollection) {
		if (roomCollection instanceof Server) {

		} else if (roomCollection instanceof PMCollection) {

		} else if (roomCollection instanceof GeneralCollection) {

		}
	}

	@FXML
	private void showDirectory() {
		client.showDirectory.set(true);
	}

}
