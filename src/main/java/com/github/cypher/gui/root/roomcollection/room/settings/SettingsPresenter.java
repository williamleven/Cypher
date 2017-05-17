package com.github.cypher.gui.root.roomcollection.room.settings;

import com.github.cypher.Settings;
import com.github.cypher.ToggleEvent;
import com.github.cypher.model.Client;
import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class SettingsPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@FXML
	private void hideRoomSettings() {
		eventBus.post(ToggleEvent.HIDE_ROOM_SETTINGS);
	}
}
