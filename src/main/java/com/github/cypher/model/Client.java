package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.Settings;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

public class Client implements Updatable {

	private final Updater updater;
	private final com.github.cypher.sdk.Client sdkClient;
	private final Settings settings;
	private final SessionManager sessionManager;

	// Servers
	private final ObservableList<Server> servers = FXCollections.observableArrayList();

	private final Map<String, User> users = new HashMap<>();

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

	public Client(com.github.cypher.sdk.Client sdkClient, Settings settings) {
		this.settings = settings;
		this.sdkClient = sdkClient;
		sessionManager = new SessionManager();

		// Loads the session file from the disk if it exists.
		if (sessionManager.savedSessionExists()) {
			Session session = sessionManager.loadSessionFromDisk();
			// If not session exists SessionManager::loadSession returns null
			if (session != null) {
				// No guarantee that the session is valid. setSession doesn't throw an exception if the session is invalid.
				sdkClient.setSession(session);
				loggedIn.setValue(true);
			}
		}

		updater = new Updater(settings.getModelTickInterval());
		updater.add(this, 1);
		updater.start();
	}

	public void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		sdkClient.login(username, password, homeserver);
	}

	public void logout() throws RestfulHTTPException, IOException {
		sdkClient.logout();
		sessionManager.deleteSessionFromDisk();
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

	public User getUser(String id) {
		if(users.containsKey(id)) {
			return users.get(id);
		}

		com.github.cypher.sdk.User sdkUser = sdkClient.getUser(id);

		User user = new User(sdkUser);
		users.put(id, user);
		return user;
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
		try {
			sdkClient.update(settings.getSDKTimeout());
		} catch (RestfulHTTPException | IOException e) {
			DebugLogger.log(e.getMessage());
		}

		//loggedIn.setValue(sdkClient.isLoggedIn());
	}

	public void exit() {
		if (settings.getSaveSession()) {
			sessionManager.saveSessionToDisk(sdkClient.getSession());
		}
		updater.interrupt();
	}
}
