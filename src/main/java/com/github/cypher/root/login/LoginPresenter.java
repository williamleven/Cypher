package com.github.cypher.root.login;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.io.IOException;

public class LoginPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField homeserverField;

	@FXML
	private CheckBox rememberMeCheckBox;

	@FXML
	private void login() {
		if (usernameField.getText() != null && passwordField.getText() != null && homeserverField.getText() != null) {

			try {
				client.login(usernameField.getText(), passwordField.getText(), homeserverField.getText());
				client.loggedIn.setValue(true);
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
		}
	}
}
