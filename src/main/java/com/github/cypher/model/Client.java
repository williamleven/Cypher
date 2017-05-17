package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import com.github.cypher.Settings;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.function.Supplier;

import static com.github.cypher.model.Util.extractServer;

public class Client implements Updatable {

	private final Supplier<com.github.cypher.sdk.Client> sdkClientFactory;

	private com.github.cypher.sdk.Client sdkClient;

	private Updater updater;
	private final Settings settings;
	private final SessionManager sessionManager;

	//RoomCollections
	private final ObservableList<RoomCollection> roomCollections =
			FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	// Servers
	private final ObservableList<Server> servers =
			FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	private Repository<User> userRepository;

	// Personal messages
	private PMCollection pmCollection;

	// General chatrooms
	private GeneralCollection genCollection;

	// Properties
	public final BooleanProperty loggedIn = new SimpleBooleanProperty();
	public final BooleanProperty showSettings = new SimpleBooleanProperty();
	public final BooleanProperty showRoomSettings = new SimpleBooleanProperty();
	public final ObjectProperty<RoomCollection> selectedRoomCollection = new SimpleObjectProperty<>();
	public final ObjectProperty<Room> selectedRoom = new SimpleObjectProperty<>();
	public final BooleanProperty showDirectory = new SimpleBooleanProperty();

	public Client(Supplier<com.github.cypher.sdk.Client> sdkClientFactory, Settings settings) {
		this.sdkClientFactory = sdkClientFactory;
		this.settings = settings;

		initialize();

		sessionManager = new SessionManager();

		// Loads the session file from the disk if it exists.
		if (sessionManager.savedSessionExists()) {
			Session session = sessionManager.loadSessionFromDisk();
			// If session doesn't exists SessionManager::loadSession returns null
			if (session != null) {
				// No guarantee that the session is valid. setSession doesn't throw an exception if the session is invalid.
				sdkClient.setSession(session);
				loggedIn.setValue(true);
				startNewUpdater();
			}
		}
		addListeners();
	}

	private void initialize() {
		pmCollection = new PMCollection();
		genCollection = new GeneralCollection();
		roomCollections.clear();
		roomCollections.add(pmCollection);
		roomCollections.add(genCollection);

		servers.clear();

		userRepository = new Repository<>((String id) -> {
			return new User(sdkClient.getUser(id));
		});

		sdkClient = sdkClientFactory.get();
		sdkClient.addJoinRoomsListener((change) -> {
			if (change.wasAdded()) {
				distributeRoom(new Room(this,change.getValueAdded()));
			}
		});

		loggedIn.set(false);
		showSettings.set(false);
		showRoomSettings.set(false);
		// GeneralCollection is set as the default selected RoomCollection
		selectedRoomCollection.set(pmCollection);
		selectedRoom.set(null);
		showDirectory.set(false);
	}

	private void addListeners() {
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
	}

	private void startNewUpdater() {
		updater = new Updater(settings.getModelTickInterval());
		updater.add(this, 1);
		updater.start();
	}

	public void login(String username, String password, String homeserver) throws SdkException{
		try {
			sdkClient.login(username, password, homeserver);
			startNewUpdater();
		}catch(RestfulHTTPException | IOException ex){
			throw new SdkException(ex);
		}
	}

	public void logout() throws SdkException{
		updater.endGracefully();
		try {
			updater.join();
			updater = null;
		} catch (InterruptedException e) {
			if (DebugLogger.ENABLED) {
				DebugLogger.log("InterruptedException when joining updater thread - " + e.getMessage());
				throw new RuntimeException("InterruptedException when joining updater thread - " + e.getMessage());
			}
		}
		try {
			sdkClient.logout();
			sessionManager.deleteSessionFromDisk();
			initialize();
		}catch(RestfulHTTPException | IOException ex){
			throw new SdkException(ex);
		}
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
		return userRepository.get(id);
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
	}

	public void exit() {
		if (settings.getSaveSession()) {
			sessionManager.saveSessionToDisk(sdkClient.getSession());
		}
		if (updater != null) {
			updater.interrupt();
		}
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
