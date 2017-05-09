package com.github.cypher.model;

import com.github.cypher.sdk.Room;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

import static com.github.cypher.model.Util.extractServer;

public class Client implements Updatable {

	private final Updater updater;

	private final com.github.cypher.sdk.Client sdkClient;

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
		updater.start();
	}

	public void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		sdkClient.login(username, password, homeserver);
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

	private void distributeRoom(Room room) {
		// Todo place in pm
		if (isPmChat(room)) {
			pmCollection.addRoom(room);
		} else {
			String mainServer = extractServer(room.getCanonicalAlias());
			addServer(mainServer);
			boolean placed = false;
			for (String alias : room.getAliases()) {
				for (Server server : servers) {
					if (server.getAddress().equals(extractServer(alias))) {
						server.addRoom(room);
						placed = true;
					}
				}
			}
			if (!placed) {
				genCollection.addRoom(room);
			}
		}

	}

	private static boolean isPmChat(Room room) {
		boolean hasName = (room.getName() != null && !room.getName().isEmpty());
		return (room.getMemberCount() < 3 && !hasName);
	}

}
