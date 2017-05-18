package com.github.cypher.gui.root.roomcollection.room.members.listitem;

import com.github.cypher.ToggleEvent;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.settings.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Member;
import com.github.cypher.gui.CustomListCell;
import com.google.common.eventbus.EventBus;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class ListItemPresenter extends CustomListCell<Member> {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@FXML
	public AnchorPane root;

	@FXML
	private ImageView imageView;

	@FXML
	private Label label;


	@FXML
	private void hideRoomSettings() {
		eventBus.post(ToggleEvent.HIDE_ROOM_SETTINGS);
	}

	@Override
	protected Node getRoot() {
		return root;
	}

	@Override
	protected void updateBindings() {
		imageView.imageProperty().bind(new FXThreadedObservableValueWrapper<>(getModelComponent().imagePropertyProperty()));
		label.textProperty().bind(new FXThreadedObservableValueWrapper<>(getModelComponent().getName()));
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
		label.textProperty().unbind();
	}
}
