package com.github.cypher.root.roomcollection.room.members;

import com.github.cypher.Settings;
import com.github.cypher.model.Client;
import com.github.cypher.model.Member;
import com.github.cypher.model.Server;
import com.github.cypher.root.roomcollection.room.members.listitem.ListItemPresenter;
import com.github.cypher.root.roomcollection.room.members.listitem.ListItemView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import javax.inject.Inject;

public class MembersPresenter {


	@Inject
	private Client client;

	@Inject
	private Settings settings;

	@FXML
	private ListView listView;
	private final ObservableList<Member> members = FXCollections.observableArrayList();


	@FXML
	private void initialize(){
		listView.setCellFactory((o) -> {
			ListItemView listItemView = new ListItemView();
			listItemView.getView();
			return (ListItemPresenter) listItemView.getPresenter();
		});
		listView.setItems(members);
		members.add(new Member("Test1"));
		members.add(new Member("Test1"));members.add(new Member("Test1"));members.add(new Member("Test1"));
	}

}
