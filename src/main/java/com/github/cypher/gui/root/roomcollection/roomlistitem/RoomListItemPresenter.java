package com.github.cypher.gui.root.roomcollection.roomlistitem;

import com.github.cypher.gui.CustomListCell;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.model.Room;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class RoomListItemPresenter extends CustomListCell<Room> {

	@Inject
	private Executor executor;

	@FXML
	private AnchorPane root;

	@FXML
	private Label name;

	@FXML
	private ImageView avatar;

	@FXML
	private Label topic;


	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Room room = getModelComponent();

		name.textProperty().bind(new FXThreadedObservableValueWrapper<>(room.nameProperty()));
		executor.handle(() -> {
			ObjectProperty<Image> image = room.avatarProperty();
			Platform.runLater(() -> {
				if (room.equals(getModelComponent())) {
					avatar.imageProperty().bind(new FXThreadedObservableValueWrapper<>(image));
				}
			});
		});
		topic.textProperty().bind(new FXThreadedObservableValueWrapper<>(room.topicProperty()));
	}

	@Override
	protected void clearBindings() {
		name.textProperty().unbind();
		avatar.imageProperty().unbind();
		avatar.imageProperty().set(null);
		topic.textProperty().unbind();
	}
}
