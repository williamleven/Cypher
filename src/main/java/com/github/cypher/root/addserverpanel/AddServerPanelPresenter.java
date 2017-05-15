package com.github.cypher.root.addserverpanel;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import com.github.cypher.root.roomcollection.directory.DirectoryView;
import com.github.cypher.root.roomcollection.room.RoomView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;

public class AddServerPanelPresenter {

	@Inject
	private Client client;

	@Inject
	private Settings settings;


	@FXML
	private void initialize() {
	}



}
