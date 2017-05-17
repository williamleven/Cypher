package com.github.cypher.gui.root.roomcollection.room;

import com.github.cypher.Settings;
import com.github.cypher.ToggleEvent;
import com.github.cypher.gui.root.roomcollection.room.chat.ChatView;
import com.github.cypher.gui.root.roomcollection.room.settings.SettingsView;
import com.github.cypher.model.Client;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.github.cypher.gui.root.roomcollection.room.members.MembersView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
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
	private AnchorPane members;

	private Parent settingsPane;
	private boolean showRoomSettings;

	@FXML
	private void initialize() {
		eventBus.register(this);

		ChatView chatView = new ChatView();
		chat.getChildren().add(chatView.getView());
		MembersView membersView = new MembersView();
		members.getChildren().add(membersView.getView());

		settingsPane = new SettingsView().getView();
		mainStackPane.getChildren().add(settingsPane);

		chatRoot.toFront();

		showRoomSettings = false;
	}

	@Subscribe
	public void toggleRoomSettings(ToggleEvent e) {
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
}
