package com.github.cypher.root.roomcollection.room.members;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import javafx.fxml.FXML;

import javax.inject.Inject;

public class MembersPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private void initialize() {
		
	}

}
