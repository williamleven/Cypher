package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class Client implements Updatable {

	private final Updater updater;

	private final com.github.cypher.sdk.Client sdkClient;

	//RoomCollections
	private final ObservableList<RoomCollection> roomCollections = FXCollections.observableArrayList();

	// Servers
	private final ObservableList<Server> servers = FXCollections.observableArrayList();

	// Personal messages
	private final PMCollection pmCollection = new PMCollection();

	// General chatrooms
	private final GeneralCollection genCollection = new GeneralCollection();

	// Properties

	public final BooleanProperty loggedIn = new SimpleBooleanProperty(false);
	public final BooleanProperty showSettings = new SimpleBooleanProperty(false);
	public final BooleanProperty showRoomSettings = new SimpleBooleanProperty(false);
	// GeneralCollection is set as the default selected RoomCollection
	public final ObjectProperty<RoomCollection> selectedRoomCollection = new SimpleObjectProperty<>(genCollection);
	//TODO: Change selectedRoom from StringProperty to "RoomProperty"
	public final StringProperty selectedRoom = new SimpleStringProperty();
	public final BooleanProperty showDirectory = new SimpleBooleanProperty(false);

	public Client(com.github.cypher.sdk.Client c) {
		sdkClient = c;
		updater = new Updater(500);
		updater.add(this, 1);
		//testcode

		roomCollections.add(pmCollection);
		roomCollections.add(genCollection);
		servers.addListener((ListChangeListener.Change<? extends Server> o) -> {
			roomCollections.clear();
			roomCollections.add(pmCollection);
			roomCollections.add(genCollection);
			roomCollections.addAll(servers);
		});
		//

		DebugLogger.log(roomCollections);
		updater.start();

	}

	// Add roomcollection, room or private chat
	public void add(String input) {
		if (Util.isHomeserver(input)) {
			addServer(input);
		} else if (Util.isRoomLabel(input)) {
			addRoom(input);
		} else if (Util.isUser(input)) {
			addUser(input);
		}
	}

	private void addServer(String server) {
		//Todo
		servers.add(new Server(server));
	}

	private void addRoom(String room) {
		//Todo
	}

	private void addUser(String user) {
		//Todo
	}

	public void update() {
		//isLoggedIn.setValue(sdkClient.isLoggedIn());
	}

	public void exit() {
		updater.interrupt();
	}

	public ObservableList<Server> getServers() {
		return servers;
	}
	public ObservableList<RoomCollection> getRoomCollections(){
		return roomCollections;
	}
}
