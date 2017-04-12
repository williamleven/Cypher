package com.github.cypher.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client implements Updatable {

	private final Updater updater;

	private final com.github.cypher.sdk.Client sdkClient;

	// Servers
	private ObservableList<Server> servers = FXCollections.observableArrayList();

	// Personal messages
	private PMCollection pmCollection = new PMCollection();

	// General chatrooms
	private GeneralCollection genCollection = new GeneralCollection();

	private BooleanProperty isLoggedIn = new SimpleBooleanProperty(false);

	Client(com.github.cypher.sdk.Client c){
		sdkClient = c;
		updater = new Updater(500);
		updater.add(this, 1);
		updater.start();
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
