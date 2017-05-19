package com.github.cypher.gui.root.login;

import com.github.cypher.settings.Settings;
import com.github.cypher.gui.Executor;
import com.github.cypher.model.Client;
import com.github.cypher.model.SdkException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import javax.inject.Inject;
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
	private TextField registrationUsernameField;

	@FXML
	private PasswordField registrationPasswordField;

	@FXML
	private TextField registrationHomeserverField;

	@FXML
	private CheckBox registrationRememberMeCheckBox;

	@FXML
	private WebView webView;

	@FXML
	private AnchorPane loginPane;

	@FXML
	private AnchorPane registerPane;

	@FXML
	public void initialize() {
		URL url = getClass().getResource("/particles/index.html");
		System.out.println();
		webView.getEngine().load(url.toString());
		webView.contextMenuEnabledProperty().set(false);
	}

	// Deinitializing has to be done when you are done with the class. Otherwise the interactive login background keeps running.
	public void deinitialize() {
		webView.getEngine().loadContent("");
	}

	@FXML
	private void switchPanes() {
		if(loginPane.isVisible()) {
			registerPane.setVisible(true);
			registerPane.toFront();
			loginPane.setVisible(false);
		} else {
			loginPane.setVisible(true);
			loginPane.toFront();
			registerPane.setVisible(false);
		}
	}

	@FXML
	private void login() {
		if (usernameField.getText() != null && passwordField.getText() != null && homeserverField.getText() != null) {
			executor.handle(() -> {
				try {
					client.login(usernameField.getText(), passwordField.getText(), homeserverField.getText());
					settings.setSaveSession(rememberMeCheckBox.isSelected());
				} catch (SdkException e) {
					System.out.printf("SdkException when trying to login - %s\n", e.getMessage());
				}
			});
		}
	}

	@FXML
	private void register() {
		if (registrationUsernameField.getText() != null && registrationPasswordField.getText() != null && registrationHomeserverField.getText() != null) {
			executor.handle(() -> {
				try {
					client.register(registrationUsernameField.getText(), registrationPasswordField.getText(), registrationHomeserverField.getText());
					settings.setSaveSession(registrationRememberMeCheckBox.isSelected());
				} catch (SdkException e) {
					System.out.printf("SdkException when trying to login - %s\n", e.getMessage());
				}
			});
		}
	}
}