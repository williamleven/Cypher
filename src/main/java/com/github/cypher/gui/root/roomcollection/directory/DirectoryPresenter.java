package com.github.cypher.gui.root.roomcollection.directory;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class DirectoryPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private void initialize() {
	}

	@FXML
	private void hideDirectory() {
		client.showDirectory.set(false);
	}
}
