package com.github.cypher.gui.root.adddialog;

import com.github.cypher.settings.Settings;
import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.model.Client;
import com.google.common.eventbus.EventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.IOException;

public class AddDialogPresenter {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@Inject
	private EventBus eventBus;

	@FXML
	public TextField serverUrlField;
	@FXML
	public Text inputValidationFeedback;

	@FXML
	public void submit(ActionEvent actionEvent) throws IOException {
		try {
			client.add(serverUrlField.getText());
			exitPane();

		} catch (IOException e) {
			inputValidationFeedback.setText(e.getMessage());
			inputValidationFeedback.setWrappingWidth(150);
		}
	}

	@FXML
	private void exitPane() {
		eventBus.post(ToggleEvent.HIDE_ADD_DIALOG);
		serverUrlField.clear();
		inputValidationFeedback.setText("");
	}

}
