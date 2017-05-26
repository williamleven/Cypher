package com.github.cypher.gui.root.roomcollection.room.memberlistitem;

import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.gui.Executor;
import com.github.cypher.gui.FXThreadedObservableValueWrapper;
import com.github.cypher.settings.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Member;
import com.github.cypher.gui.CustomListCell;
import com.google.common.eventbus.EventBus;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;

public class MemberListItemPresenter extends CustomListCell<Member> {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@Inject
	private Executor executor;

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
		Member m = getModelComponent();
		executor.handle(() -> {
			ObjectProperty<Image> avatar = m.getUser().avatarProperty();
			Platform.runLater(() -> {
				if (m.equals(getModelComponent())) {
					imageView.imageProperty().bind(new FXThreadedObservableValueWrapper<>(avatar));
				}
			});
		});
		label.textProperty().bind(new FXThreadedObservableValueWrapper<>(m.getName()));
	}

	@Override
	protected void clearBindings() {
		imageView.imageProperty().unbind();
		imageView.imageProperty().set(null);
		label.textProperty().unbind();
	}
}
