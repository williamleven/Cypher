package com.github.cypher.root.roomcollectionlistitem;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.root.CustomListCell;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;


public class RoomCollectionListItemPresenter extends CustomListCell<RoomCollection> {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private StackPane root;
	@FXML
	private ImageView imageView;

	@FXML
	private void initialize() {
	}

	@FXML
	private void toggleSettings() {
		client.showSettings.set(!client.showSettings.get());
	}

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		imageView.imageProperty().bind(getModelComponent().getImageProperty());
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
	}
}
