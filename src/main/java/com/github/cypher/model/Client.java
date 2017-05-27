package com.github.cypher.model;

import com.github.cypher.eventbus.ToggleEvent;
import com.github.cypher.settings.Settings;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.Observable;
import java.util.function.Supplier;

import static com.github.cypher.model.Util.extractServer;

public class Client {

	private final Supplier<com.github.cypher.sdk.Client> sdkClientFactory;

	private com.github.cypher.sdk.Client sdkClient;

	private Updater updater;
	private final Settings settings;
	private final EventBus eventBus;
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

	private boolean loggedIn;
	private boolean initalSyncDone;
	private RoomCollection selectedRoomCollection;
	private Room selectedRoom;

	Client(Supplier<com.github.cypher.sdk.Client> sdkClientFactory,
				  Settings settings,
				  EventBus eventBus,
				  String userDataDirectory) {

		this.sdkClientFactory = sdkClientFactory;
		this.settings = settings;
		this.eventBus = eventBus;
		eventBus.register(this);

		initialize();

		sessionManager = new SessionManager(userDataDirectory);

		// Loads the session file from the disk if it exists.
		if (sessionManager.savedSessionExists()) {
			Session session = sessionManager.loadSessionFromDisk();
			// If session doesn't exists SessionManager::loadSession returns null
			if (session != null) {
				// No guarantee that the session is valid. setSession doesn't throw an exception if the session is invalid.
				sdkClient.setSession(session);
				loggedIn = true;
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
		selectedRoomCollection = pmCollection;
		eventBus.post(selectedRoomCollection);

		servers.clear();

		userRepository = new Repository<>((String id) -> {
			return new User(sdkClient.getUser(id));
		});

		sdkClient = sdkClientFactory.get();
		sdkClient.addJoinRoomsListener((change) -> {
			if (change.wasAdded()) {
				Room room = new Room(userRepository, change.getValueAdded(), getActiveUser());
				room.aliasesList().addListener((InvalidationListener) (r) -> distributeRoom(room));
				distributeRoom(room);
			}
		});

		loggedIn = false;
		initalSyncDone = false;
		selectedRoom = null;
	}

	public User getActiveUser(){
		return getUser(sdkClient.getActiveUser().getId());
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
		updater.add(1, () -> {
			try {
				sdkClient.update(settings.getSDKTimeout());
				if (!initalSyncDone) {
					initalSyncDone = true;
					eventBus.post(ToggleEvent.HIDE_LOADING);
				}
			} catch (RestfulHTTPException | IOException e) {
				System.out.printf("%s\n", e.getMessage());
			}
		});
		updater.start();
	}

	public void login(String username, String password, String homeserver) throws SdkException{
		try {
			sdkClient.login(username, password, homeserver);
			loggedIn = true;
			eventBus.post(ToggleEvent.LOGIN);
			eventBus.post(ToggleEvent.SHOW_LOADING);
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
			System.out.printf("InterruptedException when joining updater thread - %s\n", e.getMessage());
			throw new RuntimeException("InterruptedException when joining updater thread - " + e.getMessage());
		}
		try {
			sdkClient.logout();
			sessionManager.deleteSessionFromDisk();
			eventBus.post(ToggleEvent.LOGOUT);
			initialize();
		}catch(RestfulHTTPException | IOException ex){
			throw new SdkException(ex);
		}
	}

	public void register(String username, String password, String homeserver) throws SdkException {
		try {
			sdkClient.register(username, password, homeserver);
			startNewUpdater();
		}catch(RestfulHTTPException | IOException ex){
			throw new SdkException(ex);
		}
	}


	// Add roomcollection, room or private chat
	public void add(String input) throws IOException {
		if (Util.isHomeserver(input)) {
			addServer(input);
		} else if (Util.isRoomLabel(input)) {
			try {
				addRoom(input);
			} catch (SdkException e) {

			}
		} else if (Util.isUser(input)) {
			addUser(input);
		} else {
			throw new IOException("String is neither a server, room or user id/alias.");
		}
	}

	public User getUser(String id) {
		return userRepository.get(id);
	}

	private void addServer(String serverAddress) {
		for (Server server:servers){
			if (server.getAddress().equals(serverAddress)){
				return;
			}
		}

		Server server = new Server(serverAddress);
		servers.add(server);

		// Redistrubute all rooms
		for (RoomCollection roomCollection : roomCollections.toArray(new RoomCollection[roomCollections.size()])){
			for (Room room: roomCollection.getRoomsProperty().toArray(new Room[roomCollection.getRoomsProperty().size()])) {
				distributeRoom(room);
			}
		}
	}

	private void addRoom(String room) throws SdkException{
		try {
			sdkClient.joinRoom(room);
		} catch (RestfulHTTPException | IOException e) {
			
		}
	}

	private void addUser(String user) {
		//Todo
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
		//System.out.printf("Placing %40s %40s %25s\n", room, room.getName(), room.getCanonicalAlias());
		// Place in PM
		if (room.isPmChat()) {

			// Do not list the VOIP bot room
			if (room.getMembersProperty().stream().noneMatch((member) ->
				member.getUser().getId().equals(
					"@fs_IUNsRXZndVVCRmFTcExJRk5FRTpyaWdlbC5ndXJneS5tZTo4NDQ4:matrix.org"
				)
			)){
				pmCollection.addRoom(room);
			}
		} else { // Place in servers
			if (room.getCanonicalAlias() != null){
				String mainServer = extractServer(room.getCanonicalAlias());
				addServer(mainServer);
			}
			boolean placed = false;
			boolean firstServer = true;
			for (Server server : servers) {
				boolean placedHere = false;
				for (String alias:room.aliasesList()) {
					if (firstServer){
						//System.out.printf("Alias: %50s\n", alias);
					}
					if (server.getAddress().equals(extractServer(alias))){
						pmCollection.removeRoom(room);
						genCollection.removeRoom(room);
						server.addRoom(room);
						placed = true;
						placedHere = true;
					}
				}

				if (!placedHere){
					server.removeRoom(room);
				}
				firstServer = false;
			}
			// Place in General if not placed in any server
			if (!placed) {
				genCollection.addRoom(room);
			}
		}

	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public RoomCollection getSelectedRoomCollection(){
		return selectedRoomCollection;
	}

	public Room getSelectedRoom(){
		return selectedRoom;
	}

	@Subscribe
	public void RoomCollectionChanged(RoomCollection e){
		Platform.runLater(() -> {
			this.selectedRoomCollection = e;
			System.out.printf("Selected room collection changed\n");
		});
	}

	@Subscribe
	public void RoomChanged(Room e){
		Platform.runLater(() -> {
			this.selectedRoom = e;
			System.out.printf("Selected room changed\n");
		});
	}
}
