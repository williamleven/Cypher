package com.github.cypher.gui.root.roomcollection.directory;


import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.settings.Settings;
import com.github.cypher.model.Client;
import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class DirectoryPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@FXML
	private void hideDirectory() {
		eventBus.post(ToggleEvent.HIDE_DIRECTORY);
	}
}
