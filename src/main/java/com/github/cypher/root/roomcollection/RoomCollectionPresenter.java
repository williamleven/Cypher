package com.github.cypher.root.roomcollection;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class RoomCollectionPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private SplitPane roomPane;

	@FXML
	private AnchorPane directoryPane;

	@FXML
	private void initialize() {
		client.selectedRoomCollection.addListener((observable, oldValue, newValue) -> {
			// If a new RoomCollection is selected the directory view is hidden.
			if (oldValue != null && newValue != null && oldValue != newValue) {
				client.showDirectory.set(false);
			}

			roomCollectionChanged(newValue);
		});

		client.showDirectory.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				directoryPane.toFront();
			} else {
				roomPane.toFront();
			}
		});
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
}
