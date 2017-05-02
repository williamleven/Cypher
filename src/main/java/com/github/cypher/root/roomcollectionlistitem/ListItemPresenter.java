package com.github.cypher.root.roomcollectionlistitem;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Server;
import com.github.cypher.root.CustomListCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import javax.inject.Inject;



public class ListItemPresenter extends CustomListCell<Server>{

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	Button root;

	@FXML
	ImageView imageView;

	@FXML
	private void initialize() {
	}

	@FXML
	private void toggleSettings() {
		client.showSettings.set(!client.showSettings.get());
	}

	public void click(ActionEvent actionEvent) {
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
