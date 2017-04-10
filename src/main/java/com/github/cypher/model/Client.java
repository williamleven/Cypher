package com.github.cypher.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client implements Updateable {

	private final com.github.cypher.sdk.Client sdkClient;

	private ObservableList<Server> servers = FXCollections.observableArrayList();

	private PMCollection pmCollection = new PMCollection();

	private GeneralCollection genCollection = new GeneralCollection();

	private BooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

	Client(com.github.cypher.sdk.Client c){
		sdkClient = c;
		Updater.getInstance().add(this, 1);
	}

	// Add server, room or private chat
	public void add(String input){
		if (Util.isHomeserver(input)){
			addServer(input);
		}else if (Util.isRoomLabel(input)){
			addRoom(input);
		}else if (Util.isUser(input)){
			addUser(input);
		}
	}

	private void addServer(String server){
		//Todo
		servers.add(new Server(server));
	}

	private void addRoom(String room){
		//Todo
	}

	private void addUser(String user){
		//Todo
	}

	public void update(){
		//isLoggedIn.setValue(sdkClient.isLoggedIn());
	}
}
