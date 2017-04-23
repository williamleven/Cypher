package com.github.cypher.root.roomcollection.room.chatroot;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.root.roomcollection.room.chatroot.chat.ChatView;
import com.github.cypher.root.roomcollection.room.chatroot.chatextra.ChatExtraView;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class ChatRootPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private AnchorPane leftSideAnchorPane;

	@FXML
	private AnchorPane rightSideAnchorPane;

	@FXML
	private void initialize() {
		ChatView chatView = new ChatView();
		leftSideAnchorPane.getChildren().add(chatView.getView());
		ChatExtraView chatExtraView = new ChatExtraView();
		rightSideAnchorPane.getChildren().add(chatExtraView.getView());

	}
}
