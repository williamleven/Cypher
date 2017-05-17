package com.github.cypher.gui.root.roomcollection.room.members.listitem;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Member;
import com.github.cypher.gui.CustomListCell;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class ListItemPresenter extends CustomListCell<Member> {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	public AnchorPane root;

	@FXML
	private ImageView imageView;

	@FXML
	private Label label;


	@FXML
	private void hideRoomSettings() {
		client.showRoomSettings.set(false);
	}

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		imageView.imageProperty().bind(getModelComponent().imagePropertyProperty());
		label.textProperty().bind(getModelComponent().getName());
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
		label.textProperty().unbind();
	}
}
