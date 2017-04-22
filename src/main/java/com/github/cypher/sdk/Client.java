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

	private static final String settingsNamespace = "com.github.cypher.settings";

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
	public Client(ApiLayer api) {
		this.api = api;
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
	 * Call ApiLayer.sync(...) and parse the returned data.
	 * <p>Presence-data is used to update the map of users: {@link #getUser(String)}</p>
	 * <p>Join-data is used to update the map of rooms the user has joined: {@link #getJoinRooms()}.</p>
	 * <p>All room maps are observable using the various add*RoomsListener(...) methods</p>
	 * @see com.github.cypher.sdk.api.ApiLayer#sync(String, String, boolean, User.Presence)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void update() throws RestfulHTTPException, IOException {
		JsonObject syncData = api.sync(null, lastSyncMarker, lastSyncMarker == null, User.Presence.ONLINE);

		if(syncData.has("next_batch")) {
			lastSyncMarker = syncData.get("next_batch").getAsString();
		}

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

		if(syncData.has("rooms") &&
		   syncData.get("rooms").isJsonObject()) {

			JsonObject roomsData = syncData.get("rooms").getAsJsonObject();

			if(roomsData.has("join")) {
				// Some GSON magic to convert the "join" JsonObject to an array
				HashMap<String, JsonObject> joinEvents = new Gson().fromJson(
						roomsData.get("join").getAsJsonObject(),
						new TypeToken<HashMap<String, JsonObject>>() {}.getType());
				for(Map.Entry<String, JsonObject> joinEventEntries : joinEvents.entrySet()) {
					String roomId = joinEventEntries.getKey();
					JsonObject joinEvent = joinEventEntries.getValue();
					Room room = getOrCreateRoom(joinRooms, roomId);
					room.update(joinEvent);
				}
			}

			// TODO: "leave"-rooms
			// TODO: "invite"-rooms
		}

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

	private void parseAccountDataEvent(JsonObject event) {
		if(event.has("type") &&
		   event.has("content") &&
		   event.get("type").isJsonPrimitive() &&
		   event.get("content").isJsonObject()) {
			String type = event.get("type").getAsString();

			if(type.equals(settingsNamespace)) {
				// Some GSON magic to convert the "content" JsonObject to an array
				HashMap<String, JsonElement> settings = new Gson().fromJson(
						event.get("content").getAsJsonObject(),
						new TypeToken<HashMap<String, JsonElement>>() {}.getType());
				for(Map.Entry<String, JsonElement> setting : settings.entrySet()) {

					String settingKey = setting.getKey();
					String settingValue = setting.getValue().getAsString();
					accountData.put(settingKey, settingValue);
				}
			}
		}
	}
}
