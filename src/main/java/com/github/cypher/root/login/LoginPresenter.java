package com.github.cypher.root.login;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.root.Executor;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;

public class LoginPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private Executor executor;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField homeserverField;

	@FXML
	private CheckBox rememberMeCheckBox;

	@FXML
	private WebView webView;

	@FXML
	private void initialize() {
		URL url = getClass().getResource("/particles/index.html");
		System.out.println();
		webView.getEngine().load(url.toString());
	}

	@FXML
	private void login() {
		if (usernameField.getText() != null && passwordField.getText() != null && homeserverField.getText() != null) {
			executor.handle(() -> {
				try {
					client.login(usernameField.getText(), passwordField.getText(), homeserverField.getText());
					Platform.runLater(() -> client.loggedIn.set(true));
					settings.setSaveSession(rememberMeCheckBox.isSelected());
				} catch (RestfulHTTPException e) {
					if (DebugLogger.ENABLED) {
						DebugLogger.log("RestfulHTTPException when trying to login - " + e.getMessage());
					}
				} catch (IOException e) {
					if (DebugLogger.ENABLED) {
						DebugLogger.log("IOException when trying to login - " + e.getMessage());
					}
				}
			});
		}
	}
}
