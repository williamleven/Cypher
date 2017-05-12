package com.github.cypher.root.roomcollection;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import com.github.cypher.root.roomcollection.directory.DirectoryView;
import com.github.cypher.root.roomcollection.room.RoomView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

public class RoomCollectionPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private StackPane rightSideStackPane;

	@FXML
	private ListView roomList;

	@FXML
	private void initialize() {
		Parent directoryPane = new DirectoryView().getView();
		rightSideStackPane.getChildren().add(directoryPane);
		Parent roomPane = new RoomView().getView();
		rightSideStackPane.getChildren().add(roomPane);

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

	@FXML
	private void showDirectory() {
		client.showDirectory.set(true);
	}
}
