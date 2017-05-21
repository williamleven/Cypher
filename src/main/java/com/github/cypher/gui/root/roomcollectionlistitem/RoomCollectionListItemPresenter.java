package com.github.cypher.gui.root.roomcollectionlistitem;

import com.github.cypher.model.Server;
import com.github.cypher.settings.Settings;
import com.github.cypher.gui.CustomListCell;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
	private Label letterIdentifier;

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		if (getModelComponent() instanceof Server){
			imageView.setImage(null);
			letterIdentifier.textProperty().setValue(String.valueOf(((Server) getModelComponent()).getAddress().toUpperCase().charAt(0)));
			letterIdentifier.toFront();
		}else{
			letterIdentifier.textProperty().setValue("");
			imageView.imageProperty().bind(new FXThreadedObservableValueWrapper<>(getModelComponent().getImageProperty()));
			imageView.toFront();
		}
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();

	}
}
