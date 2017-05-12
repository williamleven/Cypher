package com.github.cypher.root.roomcollectionlistitem;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import com.github.cypher.root.CustomListCell;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

	private FontIcon fontIconKeeper = new FontIcon();

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
			fontIcon.setIconColor(new Color(0,0,0,0));
			imageView.imageProperty().bind(getModelComponent().getImageProperty());
		}
		else if (getModelComponent() instanceof GeneralCollection || getModelComponent() instanceof PMCollection){
			imageView.imageProperty().unbind();
			imageView.imageProperty().setValue(null);
			if (getModelComponent() instanceof GeneralCollection){
				setIcon("fa-users");
			}
			else if(getModelComponent() instanceof PMCollection){
				setIcon("fa-wechat");
			}
		}
	}

	private void setIcon(String literal){
		fontIcon.setIconLiteral(literal);
		fontIcon.setIconSize(40);
		fontIcon.setIconColor(new Color(1,1,1,1));
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
	}
}
