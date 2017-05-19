package com.github.cypher.gui.root.loading;

import com.github.cypher.eventbus.ToggleEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;

public class LoadingPresenter {

	@Inject
	private EventBus eventBus;

	@FXML
	private ImageView imageView;

	@FXML
	private void initialize() {
		eventBus.register(this);
	}

	@Subscribe
	private void toggleLoadingScreen(ToggleEvent e) {
		Platform.runLater(() -> {
			if (e == ToggleEvent.SHOW_LOADING) {
				imageView.setImage(new Image(LoadingPresenter.class.getResourceAsStream("/images/loading.gif")));
			} else if (e == ToggleEvent.HIDE_LOADING) {
				imageView.setImage(null);
			}
		});
	}
}