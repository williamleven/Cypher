package com.github.cypher.root.roomcollectionlist;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Server;
import com.github.cypher.root.roomcollectionlist.listitem.ListItemPresenter;
import com.github.cypher.root.roomcollectionlist.listitem.ListItemView;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javax.inject.Inject;

public class RoomCollectionListPresenter{

	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private ListView roomCollectionListListView;

	private static final double SERVERLISTCELLHEIGHT=60;
	private static final double SERVERLISTCELLPADDING_BOTTOM=5;

	@FXML
	private void initialize() {
		roomCollectionListListView.setCellFactory((o) -> {
			ListItemView listItemView = new ListItemView();
			listItemView.getView();
			return (ListItemPresenter) listItemView.getPresenter();
		});


		roomCollectionListListView.setItems(client.getServers());
		client.getServers().addListener((ListChangeListener<? super Server>) (o) -> {
			updateListHeight();
		});


	}

	@FXML
	private void toggleSettings() {
		client.showSettings.set(!client.showSettings.get());
	}

	private void updateListHeight() {
		roomCollectionListListView.setPrefHeight((SERVERLISTCELLHEIGHT+SERVERLISTCELLPADDING_BOTTOM) * client.getServers().size() );
	}

}
