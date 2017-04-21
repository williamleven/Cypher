package com.github.cypher.root;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import javax.inject.Inject;

// Presenter for the root/main pane of the application
public class RootPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private AnchorPane settingsPane;

	@FXML
	private HBox serverPane;

	@FXML
	private void initialize() {
		client.showSettings.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				settingsPane.toFront();
			} else {
				serverPane.toFront();
			}
		});
	}
}
