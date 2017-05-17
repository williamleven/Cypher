package com.github.cypher.gui.root;

import com.github.cypher.DebugLogger;
import com.github.cypher.ToggleEvent;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.FXThreadedObservableListWrapper;
import com.github.cypher.gui.root.adddialog.AddDialogView;
import com.github.cypher.gui.root.login.LoginPresenter;
import com.github.cypher.gui.root.login.LoginView;
import com.github.cypher.gui.root.roomcollection.RoomCollectionView;
import com.github.cypher.gui.root.roomcollectionlistitem.RoomCollectionListItemPresenter;
import com.github.cypher.gui.root.roomcollectionlistitem.RoomCollectionListItemView;
import com.github.cypher.gui.root.settings.SettingsView;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.model.SdkException;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.util.Iterator;

// Presenter for the root/main pane of the application
public class RootPresenter {

	@Inject
	private Client client;

	@Inject
	private Executor executor;

	@Inject
	private EventBus eventBus;

	@FXML
	private StackPane mainStackPane;

	@FXML
	private StackPane rightSideStackPane;

	@FXML
	private ListView<RoomCollection> roomCollectionListView;

	private static final double ROOM_COLLECTION_LIST_CELL_HEIGHT =60;
	private static final double ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM =5;

	private Parent settingsPane;
	private boolean showSettings;
	private Parent roomCollectionPane;
	private Parent addDialogPane;
	private boolean showAddDialog;


	@FXML
	private void initialize() {
		eventBus.register(this);

		// Only load login pane if user is not already logged in
		// User might already be logged in if a valid session is available when the application is launched
		if (!client.isLoggedIn()) {
			LoginView loginPane = new LoginView();
			loginPane.getView().setUserData(loginPane.getPresenter());
			mainStackPane.getChildren().add(loginPane.getView());
		}

		settingsPane = new SettingsView().getView();
		rightSideStackPane.getChildren().add(settingsPane);
		showSettings = false;
		roomCollectionPane = new RoomCollectionView().getView();
		rightSideStackPane.getChildren().add(roomCollectionPane);
		addDialogPane = new AddDialogView().getView();
		mainStackPane.getChildren().add(addDialogPane);
		showAddDialog = false;
		addDialogPane.toBack();

		roomCollectionListView.setCellFactory(listView -> {
			RoomCollectionListItemView roomCollectionListItemView = new RoomCollectionListItemView();
			roomCollectionListItemView.getView();
			return (RoomCollectionListItemPresenter) roomCollectionListItemView.getPresenter();
		});

		roomCollectionListView.setItems(new FXThreadedObservableListWrapper<RoomCollection>(client.getRoomCollections()).getList());

		updateRoomCollectionListHeight();
		client.getRoomCollections().addListener((ListChangeListener.Change<? extends RoomCollection> change) -> {
			Platform.runLater(this::updateRoomCollectionListHeight);
		});


		roomCollectionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if ( newValue != null) {
				eventBus.post(ToggleEvent.HIDE_SETTINGS);
				eventBus.post(ToggleEvent.HIDE_ROOM_SETTINGS);
				eventBus.post(newValue);
			}
		});

	}

	@Subscribe
	public void toggleSettingsPane(ToggleEvent e){
		Platform.runLater(()-> {
			if (e == ToggleEvent.SHOW_SETTINGS && !showSettings){
				settingsPane.toFront();
				showSettings = true;
			}else if (e == ToggleEvent.HIDE_SETTINGS && showSettings){
				settingsPane.toBack();
				showSettings = false;
			}else if (e == ToggleEvent.TOGGLE_SETTINGS){
				if (showSettings){
					settingsPane.toBack();
				}else{
					settingsPane.toFront();
				}
				showSettings = !showSettings;
			}
		});
	}

	@Subscribe
	public void toggleAddDialogPane(ToggleEvent e) {
		Platform.runLater(() -> {
			if (e == ToggleEvent.SHOW_ADD_DIALOG && !showAddDialog) {
				addDialogPane.toFront();
				showAddDialog = true;
			} else if (e == ToggleEvent.HIDE_ADD_DIALOG && showAddDialog) {
				addDialogPane.toBack();
				showAddDialog = false;
			} else if (e == ToggleEvent.TOGGLE_ADD_DIALOG) {
				if (showSettings) {
					addDialogPane.toBack();
				} else {
					addDialogPane.toFront();
				}
				showAddDialog = !showAddDialog;
			}
		});
	}

	@Subscribe
	public void handleLogin(ToggleEvent e) {
		Platform.runLater(() -> {
			if (e == ToggleEvent.LOGIN) {
				// Iterators are used instead of for-each loop as the node is removed from inside the loop
				for (Iterator<Node> iter = mainStackPane.getChildren().iterator(); iter.hasNext(); ) {
					Node child = iter.next();
					if (child.getUserData() != null && child.getUserData() instanceof LoginPresenter) {
						((LoginPresenter) child.getUserData()).deinitialize();
						iter.remove();
					}
				}
			} else if (e == ToggleEvent.LOGOUT) {
				LoginView loginPane = new LoginView();
				loginPane.getView().setUserData(loginPane.getPresenter());
				mainStackPane.getChildren().add(loginPane.getView());
			}
		});
	}

	@FXML
	private void toggleSettings() {
		eventBus.post(ToggleEvent.TOGGLE_SETTINGS);
	}

	private void updateRoomCollectionListHeight() {
		roomCollectionListView.setPrefHeight((ROOM_COLLECTION_LIST_CELL_HEIGHT + ROOM_COLLECTION_LIST_CELL_PADDING_BOTTOM) * client.getRoomCollections().size() );
	}

	@FXML
	private void logout() {
		executor.handle(() -> {
			try {
				client.logout();
			} catch (SdkException e) {
				if (DebugLogger.ENABLED) {
					DebugLogger.log("SdkException when trying to logout - " + e.getMessage());
				}
			}
		});
	}

	@FXML
	public void onAddButtonAction(ActionEvent actionEvent) {
		eventBus.post(ToggleEvent.SHOW_ADD_DIALOG);
	}
}
