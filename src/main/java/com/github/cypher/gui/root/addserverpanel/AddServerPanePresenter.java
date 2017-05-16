package com.github.cypher.gui.root.addserverpanel;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class AddServerPanePresenter {

	public TextField serverUrlField;
	@Inject
	private Client client;

	@Inject
	private Settings settings;


	@FXML
	private void initialize() {
	}



}
