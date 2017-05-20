package com.github.cypher.gui.root.settings;

import com.github.cypher.gui.Executor;
import com.github.cypher.model.SdkException;
import com.github.cypher.settings.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class SettingsPresenter {
	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private Executor executor;

	@FXML
	private void initialize() {
	}

	@FXML
	private void logout() {
		executor.handle(() -> {
			try {
				client.logout();
			} catch (SdkException e) {
				System.out.printf("SdkException when trying to logout - %s\n", e.getMessage());
			}
		});
	}
}
