package com.github.cypher.gui.root.addserverpanel;

import com.github.cypher.Settings;
import com.github.cypher.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

public class AddServerPanePresenter {

	public TextField serverUrlField;
	@Inject
	private Client client;

	@Inject
	private Settings settings;


	@FXML
	private void initialize() {
	}
	private void exitPane(){
		client.showAddServersPanel.setValue(false);

	}

	public void clickBackground(MouseEvent mouseEvent) {
		exitPane();
	}

	public void clickSubmitButton(ActionEvent actionEvent) {

	}

	public void clickPanel(MouseEvent mouseEvent) {
	}

	public void clickExit(ActionEvent mouseEvent) {
		exitPane();
	}
}
