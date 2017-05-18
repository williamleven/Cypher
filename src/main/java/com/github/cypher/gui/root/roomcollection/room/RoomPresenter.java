package com.github.cypher.gui.root.roomcollection.room;

import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.gui.FXThreadedObservableListWrapper;
import com.github.cypher.gui.root.roomcollection.room.memberlistitem.MemberListItemPresenter;
import com.github.cypher.gui.root.roomcollection.room.memberlistitem.MemberListItemView;
import com.github.cypher.model.Member;
import com.github.cypher.model.Room;
import com.github.cypher.settings.Settings;
import com.github.cypher.gui.root.roomcollection.room.chat.ChatView;
import com.github.cypher.gui.root.roomcollection.room.settings.SettingsView;
import com.github.cypher.model.Client;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

public class RoomPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@FXML
	private StackPane mainStackPane;

	@FXML
	private HBox chatRoot;

	@FXML
	private AnchorPane chat;

	@FXML
	private ListView<Member> memberListView;
	private FXThreadedObservableListWrapper<Member> backendListForMemberView;

	private Parent settingsPane;
	private boolean showRoomSettings;

	@FXML
	private void initialize() {
		eventBus.register(this);

		ChatView chatView = new ChatView();
		chat.getChildren().add(chatView.getView());

		settingsPane = new SettingsView().getView();
		mainStackPane.getChildren().add(settingsPane);

		settingsPane.toBack();
		showRoomSettings = false;
	}

	@Subscribe
	private void toggleRoomSettings(ToggleEvent e) {
		if (e == ToggleEvent.SHOW_ROOM_SETTINGS && !showRoomSettings) {
			settingsPane.toFront();
			showRoomSettings = true;
		} else if (e == ToggleEvent.HIDE_ROOM_SETTINGS && showRoomSettings) {
			settingsPane.toBack();
			showRoomSettings = false;
		} else if (e == ToggleEvent.TOGGLE_ROOM_SETTINGS) {
			if (showRoomSettings) {
				settingsPane.toBack();
			} else {
				settingsPane.toFront();
			}
			showRoomSettings = !showRoomSettings;
		}
	}

	@Subscribe
	private void handleLoginStateChange(ToggleEvent e) {
		if (e == ToggleEvent.LOGOUT) {
			showRoomSettings = false;
			settingsPane.toBack();
		}
	}

	@Subscribe
	private void selectedRoomChanged(Room room) {
		Platform.runLater(() -> {
			if (backendListForMemberView != null) {
				backendListForMemberView.dispose();
			}
			backendListForMemberView = new FXThreadedObservableListWrapper<>(room.getMembersProperty());

			memberListView.setCellFactory(listView -> {
				MemberListItemView memberListItemView = new MemberListItemView();
				memberListItemView.getView();
				return (MemberListItemPresenter) memberListItemView.getPresenter();
			});

			memberListView.setItems(backendListForMemberView.getList());
		});
	}
}
