package com.github.cypher.root.login;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;

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
	private void initialize() {
	}
}
