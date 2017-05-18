package com.github.cypher.gui.root.roomcollection;

import com.github.cypher.settings.Settings;
import com.github.cypher.gui.FXThreadedObservableListWrapper;
import com.github.cypher.gui.root.roomcollection.directory.DirectoryView;
import com.github.cypher.gui.root.roomcollection.room.RoomView;
import com.github.cypher.gui.root.roomcollection.roomlistitem.RoomListItemPresenter;
import com.github.cypher.gui.root.roomcollection.roomlistitem.RoomListItemView;
import com.github.cypher.model.Client;
import com.github.cypher.model.Room;
import com.github.cypher.model.RoomCollection;
import javafx.application.Platform;
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
	private ListView<Room> roomListView;

	private FXThreadedObservableListWrapper<Room> backendListForView;

	@FXML
	private void initialize() {
		Parent directoryPane = new DirectoryView().getView();
		rightSideStackPane.getChildren().add(directoryPane);
		Parent roomPane = new RoomView().getView();
		rightSideStackPane.getChildren().add(roomPane);

		roomCollectionChanged(client.selectedRoomCollection.get());
		client.selectedRoomCollection.addListener((observable, oldValue, newValue) -> {
			// If a new RoomCollection is selected the directory view is hidden.
			if (oldValue != null && newValue != null && oldValue != newValue) {
				client.showDirectory.set(false);
			}

			roomCollectionChanged(newValue);
		});

		roomListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> client.selectedRoom.set(newValue));

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
		if (backendListForView != null) {
			backendListForView.dispose();
		}
		backendListForView = new FXThreadedObservableListWrapper<>(roomCollection.getRoomsProperty());

		roomListView.setCellFactory(listView -> {
			RoomListItemView roomListItemView = new RoomListItemView();
			roomListItemView.getView();
			return (RoomListItemPresenter) roomListItemView.getPresenter();
		});

		Platform.runLater(() -> roomListView.setItems(backendListForView.getList()));

		/*if (roomCollection instanceof Server) {

		} else if (roomCollection instanceof PMCollection) {

		} else if (roomCollection instanceof GeneralCollection) {

		}*/
	}

	@FXML
	private void showDirectory() {
		client.showDirectory.set(true);
	}
}
