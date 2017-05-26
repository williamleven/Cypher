package com.github.cypher.gui.root.roomcollection.roomlistitem;

import com.github.cypher.gui.CustomListCell;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.model.Room;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
	private TextArea topic;


	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Room room = getModelComponent();

		name.textProperty().bind(new FXThreadedObservableValueWrapper<>(room.nameProperty()));
		executor.handle(() -> {
			avatar.imageProperty().bind(new FXThreadedObservableValueWrapper<>(room.avatarProperty()));
		});
		topic.textProperty().bind(new FXThreadedObservableValueWrapper<>(room.topicProperty()));
	}

	@Override
	protected void clearBindings() {
		name.textProperty().unbind();
		avatar.imageProperty().unbind();
		topic.textProperty().unbind();
	}
}
