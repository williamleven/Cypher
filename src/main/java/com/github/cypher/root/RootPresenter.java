package com.github.cypher.root;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.model.Server;
import com.github.cypher.root.login.LoginView;
import com.github.cypher.root.roomcollection.RoomCollectionView;
import com.github.cypher.root.roomcollectionlistitem.ListItemPresenter;
import com.github.cypher.root.roomcollectionlistitem.ListItemView;
import com.github.cypher.root.settings.SettingsView;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.inject.Inject;
import java.io.IOException;

// Presenter for the root/main pane of the application
public class RootPresenter {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private Executor executor;

	@FXML
	private StackPane mainStackPane;

	@FXML
	private StackPane rightSideStackPane;

	@FXML
	private ListView roomCollectionListListView;

	private static final double ROOM_COLLECTION_LIST_CELL_HEIGHT =60;
	private static final double ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM =5;


	@FXML
	private void initialize() {
		Parent loginPane = new LoginView().getView();
		mainStackPane.getChildren().add(loginPane);


		// Hide/move login pane to back if user is already logged in.
		// This happens if a valid session is available when the application is launched.
		if (client.loggedIn.get()) {
			loginPane.toBack();
		}

		client.loggedIn.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				loginPane.toBack();
			} else {
				loginPane.toFront();
			}
		});

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



	@FXML
	private void logout() {
		executor.handle(() -> {
			try {
				client.logout();
				Platform.runLater(() -> client.loggedIn.setValue(false));
			} catch (RestfulHTTPException e) {
				if (DebugLogger.ENABLED) {
					DebugLogger.log("RestfulHTTPException when trying to logout - " + e.getMessage());
				}
			} catch (IOException e) {
				if (DebugLogger.ENABLED) {
					DebugLogger.log("IOException when trying to logout - " + e.getMessage());
				}
			}
		});
	}
}
