package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class takes an ApiLayer object and parses
 * the data it returns into usable objects
 */
public class Client {

	private ApiLayer api;
	private String lastSyncMarker = null;

	private final String settingsNamespace;

	private Map<String, User> users = new HashMap<>();

	private ObservableMap<String, String> accountData = new ObservableMapWrapper<>(new HashMap<>());

	public void addAccountDataListener   (MapChangeListener<String, String> listener) { accountData.addListener(listener);    }
	public void removeAccountDataListener(MapChangeListener<String, String> listener) { accountData.removeListener(listener); }

	private ObservableMap<String, Room> joinRooms   = new ObservableMapWrapper<>(new HashMap<>());
	private ObservableMap<String, Room> inviteRooms = new ObservableMapWrapper<>(new HashMap<>());
	private ObservableMap<String, Room> leaveRooms  = new ObservableMapWrapper<>(new HashMap<>());

	public void addJoinRoomsListener     (MapChangeListener<String, Room> listener) { joinRooms.addListener(listener);      }
	public void removeJoinRoomsListener  (MapChangeListener<String, Room> listener) { joinRooms.removeListener(listener);   }
	public void addInviteRoomsListener   (MapChangeListener<String, Room> listener) { inviteRooms.addListener(listener);    }
	public void removeInviteRoomsListener(MapChangeListener<String, Room> listener) { inviteRooms.removeListener(listener); }
	public void addLeaveRoomsListener    (MapChangeListener<String, Room> listener) { leaveRooms.addListener(listener);     }
	public void removeLeaveRoomsListener (MapChangeListener<String, Room> listener) { leaveRooms.removeListener(listener);  }

	/**
	 * @see com.github.cypher.sdk.api.ApiLayer
	 * @throws IOException
	 */
	public Client(ApiLayer api, String settingsNamespace) {
		this.api = api;
		this.settingsNamespace = settingsNamespace;
	}

	/**
	 * This object returns an existing User object if possible,
	 * otherwise it creates and caches a new one.
	 * @param userId The unique ID of the user (e.g. "@morpheus:matrix.org")
	 * @return A User object
	 */
	public User getUser(String userId) {
		if(users.containsKey(userId)) {
			return users.get(userId);
		}
		User user = new User(api, userId);
		users.put(userId, user);
		return user;
	}

	private Room getOrCreateRoom(Map<String, Room> map, String roomId) {
		if(map.containsKey(roomId)) {
			return map.get(roomId);
		}
		Room room = new Room(api, this, roomId);
		map.put(roomId, room);
		return room;
	}

	/**
	 * Call ApiLayer.login(...)
	 * @see com.github.cypher.sdk.api.ApiLayer#login(String, String, String)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		api.login(username, password, homeserver);
	}

	/**
	 * Call ApiLayer.sync(...) and parse the returned data.
	 * <p>Presence-data is used to update the map of users: {@link #getUser(String)}</p>
	 * <p>Join-data is used to update the map of rooms the user has joined: {@link #getJoinRooms()}.</p>
	 * <p>All room maps are observable using the various add*RoomsListener(...) methods</p>
	 * @see com.github.cypher.sdk.api.ApiLayer#sync(String, String, boolean, User.Presence, int)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void update(int timeout) throws RestfulHTTPException, IOException {
		JsonObject syncData = api.sync(null, lastSyncMarker, lastSyncMarker == null, User.Presence.ONLINE, timeout);

		if(syncData.has("next_batch")) {
			lastSyncMarker = syncData.get("next_batch").getAsString();
		}

		parsePresenceEvents(syncData);

		parseRoomEvents(syncData);

		parseAccountDataEvents(syncData);
	}

	/**
	 * Get the rooms which the user has joined
	 * @return A map of Room objects
	 */
	public Map<String, Room> getJoinRooms() {
		return new HashMap<>(joinRooms);
	}

	/**
	 * Get the rooms which the user has been invited to
	 * @return A map of Room objects
	 */
	public Map<String, Room> getInviteRooms() {
		return new HashMap<>(inviteRooms);
	}

	/**
	 * Get the rooms which the user has left
	 * @return A map of Room objects
	 */
	public Map<String, Room> getLeaveRooms() {
		return new HashMap<>(leaveRooms);
	}

	/**
	 * Get a map of the public rooms listed in a servers room directory
	 * @param server The matrix home server from which to read the room directory (e.g. "matrix.org", "example.org:8448")
	 * @return A map of Room objects
	 * @see com.github.cypher.sdk.api.ApiLayer#getPublicRooms(String)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public Map<String, Room> getPublicRooms(String server) throws RestfulHTTPException, IOException {
		JsonObject data = api.getPublicRooms(server);
		Map<String, Room> listedRooms = new HashMap<>();
		if(data.has("chunk") &&
		   data.get("chunk").isJsonArray()) {
			JsonArray directoryArray = data.get("chunk").getAsJsonArray();
			for(JsonElement roomElement : directoryArray) {
				if(roomElement.isJsonObject()) {
					JsonObject roomData = roomElement.getAsJsonObject();
					if(roomData.has("room_id")) {
						String roomId = roomData.get("room_id").getAsString();
						Room room = new Room(api, this, roomId);
						room.update(roomData);
						listedRooms.put(roomId, room);
					}
				}
			}
		}
		return listedRooms;
	}

	public String getSetting(String key) {
		return accountData.get(key);
	}

	public void setSetting(String key, String value) {
		// TODO
	}

	private void parsePresenceEvents(JsonObject syncData) {
		if(syncData.has("presence")) {
			JsonObject presenceData = syncData.get("presence").getAsJsonObject();
			if(presenceData.has("events")) {
				JsonArray presenceEvents = presenceData.get("events").getAsJsonArray();
				for(JsonElement eventElement : presenceEvents) {
					JsonObject eventObject = eventElement.getAsJsonObject();
					if(eventObject.has("sender")) {
						User user = getUser(eventObject.get("sender").getAsString());
						user.update(eventObject);
					}
				}
			}
		}
	}

	private void parseRoomEvents(JsonObject syncData) {
		if(syncData.has("rooms") &&
				syncData.get("rooms").isJsonObject()) {

			JsonObject roomsData = syncData.get("rooms").getAsJsonObject();

			if(roomsData.has("join") &&
					roomsData.get("join").isJsonObject()) {
				JsonObject joinEvents = roomsData.get("join").getAsJsonObject();
				for(Map.Entry<String, JsonElement> joinEventEntry : joinEvents.entrySet()) {
					if(joinEventEntry.getValue().isJsonObject()) {
						String roomId = joinEventEntry.getKey();
						JsonObject joinEvent = joinEventEntry.getValue().getAsJsonObject();
						Room room = getOrCreateRoom(joinRooms, roomId);
						room.update(joinEvent);
					}
				}
			}

			// TODO: "leave"-rooms
			// TODO: "invite"-rooms
		}
	}

	private void parseAccountDataEvents(JsonObject syncData) {
		if(syncData.has("account_data") &&
		   syncData.get("account_data").isJsonObject()) {
			JsonObject accountDataObject = syncData.get("account_data").getAsJsonObject();

			if(accountDataObject.has("events") &&
			   accountDataObject.get("events").isJsonArray()) {
				JsonArray accountDataEvents = accountDataObject.get("events").getAsJsonArray();

				for(JsonElement eventElement : accountDataEvents) {
					if(eventElement.isJsonObject()) {
						JsonObject event = eventElement.getAsJsonObject();

						parseAccountDataEvent(event);
					}
				}
			}
		}
	}

	private void parseAccountDataEvent(JsonObject event) {
		if(event.has("type") &&
		   event.has("content") &&
		   event.get("type").isJsonPrimitive() &&
		   event.get("content").isJsonObject()) {
			String type = event.get("type").getAsString();
			JsonObject eventContent = event.get("content").getAsJsonObject();

			if(type.equals(settingsNamespace)) {
				for(Map.Entry<String, JsonElement> setting : eventContent.entrySet()) {
					String settingKey = setting.getKey();
					String settingValue = setting.getValue().getAsString();
					accountData.put(settingKey, settingValue);
				}
			}
		}
	}
}
