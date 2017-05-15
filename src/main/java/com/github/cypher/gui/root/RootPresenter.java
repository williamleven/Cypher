package com.github.cypher.gui.root;

import com.github.cypher.DebugLogger;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.root.roomcollection.RoomCollectionView;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.gui.root.login.LoginPresenter;
import com.github.cypher.gui.root.login.LoginView;
import com.github.cypher.gui.root.roomcollectionlistitem.RoomCollectionListItemPresenter;
import com.github.cypher.gui.root.roomcollectionlistitem.RoomCollectionListItemView;
import com.github.cypher.gui.root.settings.SettingsView;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Iterator;

// Presenter for the root/main pane of the application
public class RootPresenter {

	@Inject
	private Client client;

	@Inject
	private Executor executor;

	@FXML
	private StackPane mainStackPane;

	@FXML
	private StackPane rightSideStackPane;

	@FXML
	private ListView<RoomCollection> roomCollectionListView;

	private static final double ROOM_COLLECTION_LIST_CELL_HEIGHT =60;
	private static final double ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM =5;


	@FXML
	private void initialize() {
		// Only load login pane if user is not already logged in
		// User might already be logged in if a valid session is available when the application is launched
		if (!client.loggedIn.get()) {
			LoginView loginPane = new LoginView();
			loginPane.getView().setUserData(loginPane.getPresenter());
			mainStackPane.getChildren().add(loginPane.getView());
		}

		client.loggedIn.addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
			if (newValue) {
				// Iterators are used instead of for-each loop as the node is removed from inside the loop
				for (Iterator<Node> iter = mainStackPane.getChildren().iterator(); iter.hasNext(); ) {
					Node child = iter.next();
					if (child.getUserData() != null && child.getUserData() instanceof LoginPresenter) {
						((LoginPresenter) child.getUserData()).deinitialize();
						iter.remove();
					}
				}
			} else {
				LoginView loginPane = new LoginView();
				loginPane.getView().setUserData(loginPane.getPresenter());
				mainStackPane.getChildren().add(loginPane.getView());
			}
		}));

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

		roomCollectionListView.setItems(client.getRoomCollections());
		roomCollectionListView.setCellFactory(listView -> {
			RoomCollectionListItemView roomCollectionListItemView = new RoomCollectionListItemView();
			roomCollectionListItemView.getView();
			return (RoomCollectionListItemPresenter) roomCollectionListItemView.getPresenter();
		});

		updateRoomCollectionListHeight();
		client.getRoomCollections().addListener((ListChangeListener.Change<? extends RoomCollection> change) -> updateRoomCollectionListHeight());

	}

	@FXML
	private void toggleSettings() {
		client.showSettings.set(!client.showSettings.get());
	}

	private void updateRoomCollectionListHeight() {
		roomCollectionListView.setPrefHeight((ROOM_COLLECTION_LIST_CELL_HEIGHT + ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM) * client.getRoomCollections().size() );
	}

	@FXML
	private void logout() {
		executor.handle(() -> {
			try {
				client.logout();
				client.loggedIn.setValue(false);
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