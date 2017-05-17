package com.github.cypher.gui.root.addserverpanel;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.IOException;

public class AddServerPanePresenter {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	public TextField serverUrlField;
	@FXML
	public Text inputValidationFeedback;

	private void exitPane(){
		client.showAddServersPanel.setValue(false);
		serverUrlField.setText("");
		inputValidationFeedback.setText("");
	}
	private void addSubmission() {
		try {
			client.add(serverUrlField.getText());
			exitPane();

		}catch (IOException e){
			inputValidationFeedback.setText(e.getMessage());
			inputValidationFeedback.setWrappingWidth(150);
		}


	}

	public void clickBackground(MouseEvent mouseEvent) {
		exitPane();
	}

	public void clickSubmitButton(ActionEvent actionEvent) throws IOException {
		addSubmission();
	}

	public void clickExit(ActionEvent mouseEvent) {
		exitPane();
	}

	public void enterPressed(ActionEvent actionEvent) {
		addSubmission();
	}
}
