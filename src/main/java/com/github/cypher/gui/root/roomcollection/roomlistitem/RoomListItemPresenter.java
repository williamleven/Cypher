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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.util.Locale;
import java.util.ResourceBundle;

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

	@FXML
	private Label unreadCounter;

	@FXML
	private Pane unreadMessagesPane;

	private final ResourceBundle bundle = ResourceBundle.getBundle(
			"com.github.cypher.gui.root.roomcollection.roomlistitem.roomlistitem",
			Locale.getDefault());


	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		Room room = getModelComponent();
		Platform.runLater(() ->{
			(new FXThreadedObservableValueWrapper<>(room.nameProperty())).addListener((invalidated) -> {
				updateRoomName(room);
			} );

			(new FXThreadedObservableValueWrapper<>(room.topicProperty())).addListener((invalidated) -> {
				updateTopicName(room);
			} );
			updateRoomName(room);
			updateTopicName(room);
		});

		executor.handle(() -> {
			ObjectProperty<Image> image = room.avatarProperty();
			Platform.runLater(() -> {
				if (room.equals(getModelComponent())) {
					avatar.imageProperty().bind(new FXThreadedObservableValueWrapper<>(image));
				}
			});
		});

	}

	@Override
	protected void clearBindings() {
		name.textProperty().unbind();
		avatar.imageProperty().unbind();
		avatar.imageProperty().set(null);
		topic.textProperty().unbind();
	}

	private void updateRoomName(Room room){
		if (room.getName() == null || room.getName().isEmpty()) {
			name.textProperty().setValue(bundle.getString("name.default"));
		}else{
			name.textProperty().setValue(room.getName());
		}
	}

	private void updateTopicName(Room room){
		if (room.getTopic() == null || room.getTopic().isEmpty()) {
			topic.textProperty().setValue(bundle.getString("topic.default"));
		}else{
			topic.textProperty().setValue(room.getTopic());
		}
	}
}
