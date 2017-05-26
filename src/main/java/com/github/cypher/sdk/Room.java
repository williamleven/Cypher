package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableMapWrapper;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * This class contains the metadata of a Matrix chat room,
 * as well as its messages and information about its members.
 * <p>
 * Room objects are returned by the methods of a Client object
 *
 * @see com.github.cypher.sdk.Client
 */
public class Room {
	private final ApiLayer api;
	private final Repository<User> userRepository;

	private final String id;
	private final StringProperty name = new SimpleStringProperty(null);
	private final StringProperty topic = new SimpleStringProperty(null);
	private final ObjectProperty<URL> avatarUrl = new SimpleObjectProperty<>(null);
	private final ObjectProperty<Image> avatar = new SimpleObjectProperty<>(null);
	private boolean avatarWanted = false;
	private final Object avatarLock = new Object();
	private final ObjectProperty<PermissionTable> permissions = new SimpleObjectProperty<>(null);
	private int avatarSize=24;

	private URL loadedAvatarUrl = null;
	private int loadedAvatarSize = 0;

	private final ObservableMap<String, Event> events =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final ObservableList<Member> members =
		FXCollections.synchronizedObservableList(new ObservableListWrapper<Member>(new ArrayList<>()));

	private final ObservableList<String> aliases =
		FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	private final ObservableMap<String, ObservableList<String>> aliasLists =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final StringProperty canonicalAlias = new SimpleStringProperty();

	private List<ChangeListener<? super Image>> avatarListeners = new ArrayList<>();

	Room(ApiLayer api, Repository<User> userRepository, String id) {
		this.api = api;
		this.userRepository = userRepository;
		this.id = id;

		// update the alias list
		aliasLists.addListener((InvalidationListener) (invalidated) -> {
			// get all aliases
			java.util.List<String> list = new ArrayList<>();
			for (ObservableList<String> a:aliasLists.values()){
				list.addAll(a);
			}

			// set aliases
			aliases.setAll(list);
		});
	}

	public void addEventListener(MapChangeListener<String, Event> listener) {
		events.addListener(listener);
	}

	public void removeEventListener(MapChangeListener<String, Event> listener) {
		events.removeListener(listener);
	}

	public void addMemberListener(ListChangeListener<Member> listener) {
		members.addListener(listener);
	}

	public void removeMemberListener(ListChangeListener<Member> listener) {
		members.removeListener(listener);
	}

	public void addAliasesListener(ListChangeListener<String> listener) {
		aliases.addListener(listener);
	}

	public void removeAliasesListener(ListChangeListener<String> listener) {
		aliases.removeListener(listener);
	}

	public void addCanonicalAliasListener(ChangeListener<String> listener) {
		canonicalAlias.addListener(listener);
	}

	public void removeCanonicalAliasListener(ChangeListener<String> listener) {
		canonicalAlias.removeListener(listener);
	}

	void update(JsonObject data) {
		parseNameData(data);

		parseTopicData(data);

		parseAvatarUrlData(data);

		parseEvents("timeline", data);

		parseEvents("state", data);
	}

	private void parseEvents(String category, JsonObject data) {
		if (data.has(category) &&
		    data.get(category).isJsonObject()) {
			JsonObject timeline = data.get(category).getAsJsonObject();
			if (timeline.has("events") &&
			    timeline.get("events").isJsonArray()) {
				JsonArray events = timeline.get("events").getAsJsonArray();
				for (JsonElement eventElement : events) {
					if (eventElement.isJsonObject()) {
						parseEventData(eventElement.getAsJsonObject());
					}
				}
			}
		}
	}

	private void parseEventData(JsonObject event) {
		if (event.has("type") &&
		    event.has("origin_server_ts") &&
		    event.has("sender") &&
		    event.has("event_id") &&
		    event.has("content")) {

			int originServerTs = event.get("origin_server_ts").getAsInt();
			String sender = event.get("sender").getAsString();
			String eventId = event.get("event_id").getAsString();
			String eventType = event.get("type").getAsString();
			int age = 0;

			if(event.has("unsigned") &&
			   event.get("unsigned").isJsonObject()) {
				JsonObject unsigned = event.get("unsigned").getAsJsonObject();

				if(unsigned.has("age")) {
					age = unsigned.get("age").getAsInt();
				}
			}

			JsonObject content = event.get("content").getAsJsonObject();

			if        ("m.room.message".equals(eventType)) {
				parseMessageEvent(originServerTs, sender, eventId, age, content);
			} else if ("m.room.member".equals(eventType)) {
				parseMemberEvent(event, originServerTs, sender, eventId, age, content);
			} else if ("m.room.name".equals(eventType)) {
				parseNameData(content);
				addPropertyChangeEvent(originServerTs, sender, eventId, age, "name", name.getValue());
			} else if ("m.room.topic".equals(eventType)) {
				parseTopicData(content);
				addPropertyChangeEvent(originServerTs, sender, eventId, age, "topic", topic.getValue());
			} else if ("m.room.avatar".equals(eventType)) {
				parseAvatarUrlData(content);
				addPropertyChangeEvent(originServerTs, sender, eventId, age, "avatar_url", avatarUrl.getValue());
			} else if ("m.room.aliases".equals(eventType)) {
				if (event.has("state_key") &&
					event.get("state_key").isJsonPrimitive()){
					parseAliasesEvent(content, event.get("state_key").getAsString());
				}
			} else if ("m.room.canonical_alias".equals(eventType)) {
				parseCanonicalAlias(content);
			} else if ("m.room.power_levels".equals(eventType)) {
				parsePowerLevelsEvent(content);
				addPropertyChangeEvent(originServerTs, sender, eventId, age, "power_levels", permissions.getValue());
			}
		}
	}

	private void parseCanonicalAlias(JsonObject content) {
		if (content.has("alias") &&
		    content.get("alias").isJsonPrimitive() &&
		   !content.get("alias").getAsString().isEmpty()) {
			canonicalAlias.setValue(content.get("alias").getAsString());
		}
	}

	private void parseAliasesEvent(JsonObject content, String stateKey) {
		if (content.has("aliases") &&
		    content.get("aliases").isJsonArray()) {

			JsonArray aliases = content.getAsJsonArray("aliases");

			ObservableList<String> list =
				FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

			for (JsonElement alias : aliases) {
				list.add(alias.getAsString());
			}
			if (list.size() == 0){
				this.aliasLists.remove(stateKey);
			}else{
				this.aliasLists.put(stateKey, list);
			}
		}
	}

	private void parseNameData(JsonObject data) {
		if (data.has("name")) {
			name.set(data.get("name").getAsString());
		}
	}

	private void parseTopicData(JsonObject data) {
		if (data.has("topic")) {
			topic.set(data.get("topic").getAsString());
		}
	}


	private void parseAvatarUrlData(JsonObject data) {
		for(String key : new String[] {"url", "avatar_url"}) {
			if (data.has(key)) {
				try {
					URL newAvatarUrl = new URL(data.get(key).getAsString());
					if (!newAvatarUrl.equals(avatarUrl.getValue())) {
						this.avatarUrl.set(newAvatarUrl);
						updateAvatar();
					}
					break;
				} catch(RestfulHTTPException | IOException e) {
					avatarUrl.set(null);
				}
			}
		}
	}

	private void updateAvatar() throws RestfulHTTPException, IOException{
		synchronized (avatarLock) {
			if (avatarWanted && avatarUrl.get() != null ) {
				if (!avatarUrl.get().equals(loadedAvatarUrl) || avatarSize > loadedAvatarSize){
					avatar.set(ImageIO.read(api.getMediaContentThumbnail(avatarUrl.getValue(),avatarSize)));
					loadedAvatarUrl = avatarUrl.get();
					loadedAvatarSize = avatarSize;
				}
			} else {
				avatar.set(null);
			}
		}
	}

	private void parseMessageEvent(int originServerTs, String sender, String eventId, int age, JsonObject content) {
		User author = userRepository.get(sender);
		this.events.put(
			eventId,
			new Message(api, originServerTs, author, eventId, age, content)
		               );
	}

	private void parseMemberEvent(JsonObject event, int originServerTs, String senderId, String eventId, int age, JsonObject content) {
		if (content.has("membership") &&
		    event.has("state_key")) {
			String memberId = event.get("state_key").getAsString();
			String membership = content.get("membership").getAsString();

			User user = userRepository.get(memberId);
			user.update(event);

			if        ("join".equals(membership)) {
				if (members.stream().noneMatch(m -> m.getUser().getId().equals(memberId))) {
					members.add(new Member(user));
				}
			} else if ("leave".equals(membership)) {
				Optional<Member> optionalMember = members.stream().filter(m -> m.getUser().getId().equals(memberId)).findAny();
				optionalMember.ifPresent(member -> members.remove(member));
			}

			// Add membership event to the log
			User sender = userRepository.get(senderId);
			events.put(
					eventId,
					new MemberEvent(api, originServerTs, sender, eventId, age, memberId, membership)
			);
		}
	}

	private void parsePowerLevelsEvent(JsonObject data) {
		try {
			this.permissions.set(new PermissionTable(data));
		} catch(IOException e) {
			this.permissions.set(null);
		}

		if(data.has("users") &&
		   data.get("users").isJsonObject()) {

			for(Map.Entry<String, JsonElement> userPowerEntry : data.get("users").getAsJsonObject().entrySet()) {
				String memberId = userPowerEntry.getKey();
				Optional<Member> optionalMember = members.stream().filter(m -> m.getUser().getId().equals(memberId)).findAny();
				optionalMember.ifPresent(member -> member.privilegeProperty().setValue(userPowerEntry.getValue().getAsInt()));
			}
		}
	}

	private <T> void addPropertyChangeEvent(int originServerTs, String senderId, String eventId, int age, String property, T value) {
		User sender = userRepository.get(senderId);
		events.put(
				eventId,
				new PropertyChangeEvent<>(api, originServerTs, sender, eventId, age, property, value)
		);
	}

	/**
	 * Send a message event of the type "m.text" to this Matrix chat room
	 *
	 * @param message The message body
	 * @throws RestfulHTTPException
	 * @throws IOException
	 * @see com.github.cypher.sdk.api.ApiLayer#roomSendEvent(String, String, JsonObject)
	 */
	public void sendTextMessage(String message) throws RestfulHTTPException, IOException {
		JsonObject content = new JsonObject();
		content.addProperty("msgtype", "m.text");
		content.addProperty("body", message);
		sendMessage(content);
	}

	/**
	 * Send a custom message event to this Matrix chat room
	 *
	 * @param content The json object containing the message
	 * @throws RestfulHTTPException
	 * @throws IOException
	 * @see com.github.cypher.sdk.api.ApiLayer#roomSendEvent(String, String, JsonObject)
	 */
	public void sendMessage(JsonObject content) throws RestfulHTTPException, IOException {
		api.roomSendEvent(this.id, "m.room.message", content);
	}

	public void addNameListener(ChangeListener<? super String> listener) {
		name.addListener(listener);
	}

	public void removeNameListener(ChangeListener<? super String> listener) {
		name.removeListener(listener);
	}

	public void addTopicListener(ChangeListener<? super String> listener) {
		topic.addListener(listener);
	}

	public void removeTopicListener(ChangeListener<? super String> listener) {
		topic.removeListener(listener);
	}

	public void addAvatarUrlListener(ChangeListener<? super URL> listener) {
		avatarUrl.addListener(listener);
	}

	public void removeAvatarUrlListener(ChangeListener<? super URL> listener) {
		avatarUrl.removeListener(listener);
	}

	public void addAvatarListener(ChangeListener<? super Image> listener, int size) {
		//synchronized (avatarLock) {
			avatarListeners.add(listener);
			avatarWanted = true;
			if (size > avatarSize) {
				avatarSize = size;
			}
			try {
				updateAvatar();
			} catch (RestfulHTTPException | IOException e) { /* Nothing */}

			avatar.addListener(listener);
		//}
	}

	public void removeAvatarListener(ChangeListener<? super Image> listener) {
		//synchronized (avatarLock) {
			avatarListeners.remove(listener);
			if (avatarListeners.isEmpty()) {
				avatarWanted = false;
			}
			avatar.removeListener(listener);
		//}
	}

	/**
	 * @return A valid room ID (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 */
	public String getId()        { return this.id; }
	public String getName()      { return name.get(); }
	public String getTopic()     { return topic.get(); }
	public URL    getAvatarUrl() { return avatarUrl.get(); }
	public Image getAvatar(int size)    {
		synchronized (avatarLock) {
			boolean old = avatarWanted;
			avatarWanted = true;
			if (size > avatarSize) {
				avatarSize = size;
			}
			try {
				updateAvatar();
			} catch (RestfulHTTPException | IOException e) { /* Nothing */}
			avatarWanted = old;
			return avatar.get();
		}
	}

	public Map<String, Event> getEvents() { return new HashMap<>(events); }
	public int getEventCount() { return events.size(); }

	public List<Member> getMembers() { return new ArrayList<>(members); }
	public int getMemberCount() { return members.size(); }

	public String[] getAliases() { return aliases.toArray(new String[aliases.size()]); }
	public String getCanonicalAlias() { return canonicalAlias.get(); }
}
