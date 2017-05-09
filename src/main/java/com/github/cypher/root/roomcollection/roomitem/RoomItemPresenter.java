package com.github.cypher.root.roomcollection.roomitem;

import com.github.cypher.DebugLogger;
import com.github.cypher.model.Room;
import com.github.cypher.root.CustomListCell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class RoomItemPresenter extends CustomListCell<Room> {

	@FXML
	private AnchorPane root;

	@FXML
	private Label name;

	@FXML
	private ImageView avatar;

	@FXML
	private TextArea topic;


	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Room room = getModelComponent();

		name.textProperty().bind(room.nameProperty());
		avatar.imageProperty().bind(room.avatarProperty());
		topic.textProperty().bind(room.topicProperty());
	}

	@Override
	protected void clearBindings() {
		name.textProperty().unbind();
		avatar.imageProperty().unbind();
		topic.textProperty().unbind();
	}
}
