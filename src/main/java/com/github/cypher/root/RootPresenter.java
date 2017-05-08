package com.github.cypher.root;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.root.login.LoginView;
import com.github.cypher.root.roomcollection.RoomCollectionView;
import com.github.cypher.root.settings.SettingsView;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.io.IOException;

// Presenter for the root/main pane of the application
public class RootPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private Executor executor;

	@FXML
	private StackPane mainStackPane;

	@FXML
	private StackPane rightSideStackPane;


	@FXML
	private void initialize() {
		Parent loginPane = new LoginView().getView();
		mainStackPane.getChildren().add(loginPane);


		// Hide/move login pane to back if user is already logged in.
		// This happens if a valid session is available when the application is launched.
		if (client.loggedIn.get()) {
			loginPane.toBack();
		}

		client.loggedIn.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				loginPane.toBack();
			} else {
				loginPane.toFront();
			}
		});

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
	}

	@FXML
	private void toggleSettings() {
		client.showSettings.set(!client.showSettings.get());
	}

	@FXML
	private void logout() {
		executor.handle(() -> {
			try {
				client.logout();
				Platform.runLater(() -> client.loggedIn.setValue(false));
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
