package com.github.cypher.root;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.root.roomcollection.RoomCollectionView;
import com.github.cypher.root.roomcollectionlist.RoomCollectionListView;
import com.github.cypher.root.settings.SettingsView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

// Presenter for the root/main pane of the application
public class RootPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private AnchorPane leftSideAnchorPane;


	@FXML
	private StackPane rightSideStackPane;

	@FXML
	private void initialize() {
		RoomCollectionListView roomCollectionListView = new RoomCollectionListView();
		leftSideAnchorPane.getChildren().add(roomCollectionListView.getView());

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
	}
}
