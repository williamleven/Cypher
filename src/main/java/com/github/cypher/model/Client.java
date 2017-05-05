package com.github.cypher.model;

import com.github.cypher.Settings;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

public class Client implements Updatable {

	private final Updater updater;
	private final com.github.cypher.sdk.Client sdkClient;
	private final Settings settings;
	private final SessionManager sessionManager;

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

	public Client(com.github.cypher.sdk.Client c, Settings settings) {
		sdkClient = c;
		this.settings = settings;
		sessionManager = new SessionManager(sdkClient);

		// Loads the session file from the disk if it exists.
		if (sessionManager.savedSessionExists()) {
			Session session = sessionManager.loadSession();
			// If not session exists SessionManager::loadSession returns null
			if (session != null) {
				sdkClient.setSession(session);
				// Checks if the loaded session is valid. If it is, sets logged in to true.
				if (sdkClient.validateCurrentSession()) {
					loggedIn.setValue(true);
				} else {
					//TODO logout? setSession(null)?
				}
			}
		}

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
		if (settings.getSaveSession()) {
			sessionManager.saveSession();
		}
		updater.interrupt();
	}
}
