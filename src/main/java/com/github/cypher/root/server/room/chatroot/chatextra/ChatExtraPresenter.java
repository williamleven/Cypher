package com.github.cypher.root.server.room.chatroot.chatextra;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;

// Controller for the root/main pane of the application
public class ChatExtraPresenter {

	@FXML
	Label label;

	@Inject
	private Integer n1;

	@Inject
	private String s1;

	@FXML
	private void initialize() {
		label.setText(s1);
	}
}
