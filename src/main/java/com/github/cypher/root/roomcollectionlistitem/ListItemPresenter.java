package com.github.cypher.root.roomcollectionlistitem;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.RoomCollection;
import com.github.cypher.model.Server;
import com.github.cypher.root.CustomListCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.inject.Inject;


public class ListItemPresenter extends CustomListCell<RoomCollection>{

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	Button root;

	private ImageView imageView = new ImageView();

	@FXML
	private void initialize() {
		if (getModelComponent() instanceof Server){

			imageView.setImage(new Image("https://hue.chalmers.it/assets/it-logo-54fcdb4210cc6e5f62676fee4e585a80.png"));
			root = new Button("",imageView);
		}
		else {
			root = new Button("",imageView);
		}
		root.getStyleClass().addAll("btn-info","btn","image-btn");
		root.setPrefSize(60,60);
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
