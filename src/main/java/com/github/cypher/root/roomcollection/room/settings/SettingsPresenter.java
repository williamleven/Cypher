package com.github.cypher.root.roomcollection.room.settings;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class SettingsPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private void initialize() {}

	@FXML
	private void hideRoomSettings() {
		client.showRoomSettings.set(false);
	}
}
