package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.collections.FXCollections;
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

	private final ApiLayer api;
	private String lastSyncMarker = null;

	private final String settingsNamespace;

	private final Repository<User> users;

	private final ObservableMap<String, String> accountData =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final ObservableMap<String, Room> joinRooms =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final ObservableMap<String, Room> inviteRooms =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final ObservableMap<String, Room> leaveRooms =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	public void addJoinRoomsListener     (MapChangeListener<String, Room> listener) { joinRooms.addListener(listener);      }
	public void removeJoinRoomsListener  (MapChangeListener<String, Room> listener) { joinRooms.removeListener(listener);   }
	public void addInviteRoomsListener   (MapChangeListener<String, Room> listener) { inviteRooms.addListener(listener);    }
	public void removeInviteRoomsListener(MapChangeListener<String, Room> listener) { inviteRooms.removeListener(listener); }
	public void addLeaveRoomsListener    (MapChangeListener<String, Room> listener) { leaveRooms.addListener(listener);     }
	public void removeLeaveRoomsListener (MapChangeListener<String, Room> listener) { leaveRooms.removeListener(listener);  }

	public void addAccountDataListener   (MapChangeListener<String, String> listener) { accountData.addListener(listener);    }
	public void removeAccountDataListener(MapChangeListener<String, String> listener) { accountData.removeListener(listener); }

	/**
	 * @see com.github.cypher.sdk.api.ApiLayer
	 * @throws IOException
	 */
	Client(ApiLayer api, String settingsNamespace) {
		this.api = api;
		this.settingsNamespace = settingsNamespace;
		users = new Repository<>((String id) -> {
			return new User(api, id);
		});
	}

	public Session getSession() {
		return  api.getSession();
	}

	public User getUser(String id){
		return this.users.get(id);
	}

	public void setSession(Session session) {
		api.setSession(session);
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
	 * Call ApiLayer.logout(...)
	 * @see com.github.cypher.sdk.api.ApiLayer#logout()
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void logout() throws RestfulHTTPException, IOException {
		api.logout();
	}

	/**
	 * Call ApiLayer.register(...)
	 * @see com.github.cypher.sdk.api.ApiLayer#register(String, String, String)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void register(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		api.register(username, password, homeserver);
	}


	/**
	 * Call ApiLayer.sync(...) and parse the returned data.
	 * <p>Presence-data is used to update the map of users: {@link #getUser(String)}</p>
	 * <p>Join-data is used to update the map of rooms the user has joined: {@link #getJoinRooms()}.</p>
	 * <p>All room maps are observable using the various add*RoomsListener(...) methods</p>
	 * @see com.github.cypher.sdk.api.ApiLayer#sync(String, String, boolean, ApiLayer.Presence, int)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void update(int timeout) throws RestfulHTTPException, IOException {
		JsonObject syncData = api.sync(null, lastSyncMarker, lastSyncMarker == null, ApiLayer.Presence.ONLINE, timeout);

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
						Room room = new Room(api, users, roomId);
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

	public User getActiveUser(){
		return getUser(getSession().getUserId());
	}

	private void parsePresenceEvents(JsonObject syncData) {
		if(syncData.has("presence")) {
			JsonObject presenceData = syncData.get("presence").getAsJsonObject();
			if(presenceData.has("events")) {
				JsonArray presenceEvents = presenceData.get("events").getAsJsonArray();
				for(JsonElement eventElement : presenceEvents) {
					JsonObject eventObject = eventElement.getAsJsonObject();
					if(eventObject.has("sender")) {
						User user = users.get(eventObject.get("sender").getAsString());
						user.update(eventObject);
					}
				}
			}
		}
	}

	private void parseRoomEvents(JsonObject syncData) throws RestfulHTTPException, IOException {
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
						Room room;
						if(joinRooms.containsKey(roomId)) {
							room = joinRooms.get(roomId);
						}else{
							room = new Room(api, users, roomId);
						}
						room.update(joinEvent);
						joinRooms.put(roomId, room);
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
