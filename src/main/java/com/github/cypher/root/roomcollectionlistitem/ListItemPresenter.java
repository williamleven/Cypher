package com.github.cypher.root.roomcollectionlistitem;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import com.github.cypher.root.CustomListCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.inject.Inject;


public class ListItemPresenter extends CustomListCell<RoomCollection> {

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private StackPane root;
	@FXML
	private ImageView imageView;
	@FXML
	private FontIcon fontIcon;

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
		if (getModelComponent() instanceof Server) {
			imageView.imageProperty().bind(getModelComponent().getImageProperty());
			getModelComponent().getImageProperty().setValue(new Image("file:../../../../../../../../../../Users/Feffe/Pictures/nichibros-11h.jpg"));
		}
		else if (getModelComponent() instanceof GeneralCollection){
			fontIcon.setIconLiteral("fa-users");
			fontIcon.setIconSize(40);
		}
		else if (getModelComponent() instanceof PMCollection){
			fontIcon.setIconLiteral("fa-wechat");
			fontIcon.setIconSize(40);
		}
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
	}
}
