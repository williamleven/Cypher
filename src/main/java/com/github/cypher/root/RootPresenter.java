package com.github.cypher.root;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.model.Server;
import com.github.cypher.root.roomcollection.RoomCollectionView;
import com.github.cypher.root.roomcollectionlistitem.ListItemPresenter;
import com.github.cypher.root.roomcollectionlistitem.ListItemView;
import com.github.cypher.root.settings.SettingsView;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

// Presenter for the root/main pane of the application
public class RootPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private StackPane rightSideStackPane;

	@FXML
	private ListView roomCollectionListListView;

	private static final double ROOM_COLLECTION_LIST_CELL_HEIGHT =60;
	private static final double ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM =5;

	@FXML
	private void initialize() {



		Parent settingsPane = new SettingsView().getView();
		rightSideStackPane.getChildren().add(settingsPane);
		Parent roomCollectionPane = new RoomCollectionView().getView();
		rightSideStackPane.getChildren().add(roomCollectionPane);

		client.showSettings.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				settingsPane.toFront();
			} else {
				roomCollectionPane.toFront();
			}
		});
		roomCollectionListListView.setCellFactory((o) -> {
			ListItemView listItemView = new ListItemView();
			listItemView.getView();
			return (ListItemPresenter) listItemView.getPresenter();
		});


		roomCollectionListListView.setItems(client.getRoomCollections());
		updateListHeight();
		client.getRoomCollections().addListener((ListChangeListener<? super RoomCollection>) (o) -> {
			updateListHeight();
		});

	}

	@FXML
	private void toggleSettings() {
		client.showSettings.set(!client.showSettings.get());
	}

	private void updateListHeight() {
		roomCollectionListListView.setPrefHeight((ROOM_COLLECTION_LIST_CELL_HEIGHT + ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM) * client.getRoomCollections().size() );
	}

	public void onAction(ActionEvent actionEvent) {
		client.getServers().add(new Server("test"));
	}
}
