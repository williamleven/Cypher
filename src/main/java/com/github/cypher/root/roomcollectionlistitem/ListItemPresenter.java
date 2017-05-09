package com.github.cypher.root.roomcollectionlistitem;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.model.Server;
import com.github.cypher.root.CustomListCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.inject.Inject;


public class ListItemPresenter extends CustomListCell<RoomCollection> {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	AnchorPane root;
	@FXML
	private ImageView imageView;

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
		getModelComponent().getImageProperty().setValue(new Image("file:../../../../../../../../../../Users/Feffe/Pictures/nichibros-11h.jpg"));
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
	}
}
