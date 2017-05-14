package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.cypher.model.Util.extractServer;

public class Client implements Updatable {

	private final Updater updater;
	private final com.github.cypher.sdk.Client sdkClient;
	private final Settings settings;
	private final SessionManager sessionManager;

	//RoomCollections
	private final ObservableList<RoomCollection> roomCollections =
			FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	// Servers
	private final ObservableList<Server> servers =
			FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	private final Map<String, User> users = new ConcurrentHashMap<>();

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
	public final ObjectProperty<Room> selectedRoom = new SimpleObjectProperty<>(null);
	public final BooleanProperty showDirectory = new SimpleBooleanProperty(false);

	public Client(com.github.cypher.sdk.Client sdkClient, Settings settings) {
		this.sdkClient = sdkClient;
		this.settings = settings;

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

		sdkClient.addJoinRoomsListener((change) -> {
			if (change.wasAdded()) {
				distributeRoom(new Room(change.getValueAdded()));
			}
		});

		updater = new Updater(settings.getModelTickInterval());
		updater.add(this, 1);
		roomCollections.add(pmCollection);
		roomCollections.add(genCollection);
		servers.addListener((ListChangeListener.Change<? extends Server> change) -> {
			while(change.next()) {
				if (change.wasAdded()) {
					roomCollections.addAll(change.getAddedSubList());
				}
				if (change.wasRemoved()) {
					roomCollections.removeAll(change.getRemoved());
				}
			}
		});
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

	public ObservableList<RoomCollection> getRoomCollections(){
		return roomCollections;
	}

	public ObservableList<Server> getServers() {
		return servers;
	}

	private void distributeRoom(Room room) {
		// Place in PM
		if (isPmChat(room)) {
			pmCollection.addRoom(room);
		} else { // Place in servers
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
			// Place in General if not placed in any server
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
