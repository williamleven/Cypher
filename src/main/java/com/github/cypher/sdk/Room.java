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

	private String earliestBatch = null;

	private final ObservableMap<String, Event> events =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	// The latest state event for every event type
	private final ObservableMap<String, Event> latestStateEvents =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final ObservableList<Member> members =
		FXCollections.synchronizedObservableList(new ObservableListWrapper<Member>(new ArrayList<>()));

	private final ObservableList<String> aliases =
		FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

	private final ObservableMap<String, ObservableList<String>> aliasLists =
		FXCollections.synchronizedObservableMap(new ObservableMapWrapper<>(new HashMap<>()));

	private final StringProperty canonicalAlias = new SimpleStringProperty();

	private final List<ChangeListener<? super Image>> avatarListeners = new ArrayList<>();

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
		parseBasicPropertiesData(data);

		parseEventData(data);

		parseTimelineEvents(data);

		parseStateEvents(data);
	}

	/**
	 * Try to download older events from the room event log
	 * @param limit The maximum amount of events to get
	 * @return false if all history is loaded, otherwise true
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public boolean getEventHistory(Integer limit) throws RestfulHTTPException, IOException {
		if(earliestBatch == null) {
			throw new IOException("sdk.Room.getHistory(...) called before first sdk.Client.sync(...)");
		}

		JsonObject data = api.getRoomMessages(id, earliestBatch, null, true, limit);

		if(data.has("end") &&
		   data.get("end").isJsonPrimitive()) {

			earliestBatch = data.get("end").getAsString();
		} else {
			throw new IOException("No pagination token was given by the server");
		}

		if(data.has("chunk") &&
		   data.get("chunk").isJsonArray()) {

			JsonArray chunk = data.get("chunk").getAsJsonArray();

			if (chunk.size() == 0) {
				return false;
			}

			for(JsonElement eventElement : chunk) {
				if(eventElement.isJsonObject()) {
					parseEventData(eventElement.getAsJsonObject());
				}
			}
		}

		return true;
	}

	private void parseBasicPropertiesData(JsonObject data) {
		if (data.has("name")) {
			name.setValue(data.get("name").getAsString());
		}
		if (data.has("topic")) {
			topic.setValue(data.get("topic").getAsString());
		}
		if (data.has("avatar_url")) {
			try {
				URL newAvatarUrl = new URL(data.get("avatar_url").getAsString());
				avatar.set(ImageIO.read(api.getMediaContent(newAvatarUrl)));
				this.avatarUrl.set(newAvatarUrl);
			} catch(RestfulHTTPException | IOException e) {
				avatar.set(null);
				avatarUrl.set(null);
			}
		}
	}

	private void parseTimelineEvents(JsonObject data) {
		if (data.has("timeline") &&
		    data.get("timeline").isJsonObject()) {
			JsonObject timeline = data.get("timeline").getAsJsonObject();

			if(earliestBatch == null &&
			   timeline.has("prev_batch") &&
			   timeline.get("prev_batch").isJsonPrimitive()) {

				earliestBatch = timeline.get("prev_batch").getAsString();
			}

			parseEvents(timeline);
		}
	}

	private void parseStateEvents(JsonObject data) {
		if (data.has("state") &&
		    data.get("state").isJsonObject()) {
			JsonObject timeline = data.get("state").getAsJsonObject();
			parseEvents(timeline);
		}
	}

	private void parseEvents(JsonObject timeline) {
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
				parseNameData(content, originServerTs, sender, eventId, age);
			} else if ("m.room.topic".equals(eventType)) {
				parseTopicData(content, originServerTs, sender, eventId, age);
			} else if ("m.room.avatar".equals(eventType)) {
				parseAvatarUrlData(content, originServerTs, sender, eventId, age);
			} else if ("m.room.aliases".equals(eventType)) {
				if (event.has("state_key") &&
					event.get("state_key").isJsonPrimitive()){
					parseAliasesEvent(content, event.get("state_key").getAsString(), originServerTs, sender, eventId, age);
				}
			} else if ("m.room.canonical_alias".equals(eventType)) {
				parseCanonicalAlias(content, originServerTs, sender, eventId, age);
			} else if ("m.room.power_levels".equals(eventType)) {
				parsePowerLevelsEvent(content, originServerTs, sender, eventId, age);
			}
		}
	}

	private void parseCanonicalAlias(JsonObject content, int originServerTs, String sender, String eventId, int age) {
		if (content.has("alias") &&
		    content.get("alias").isJsonPrimitive() &&
		   !content.get("alias").getAsString().isEmpty()) {
			String newAlias = "";
			Event event = addPropertyChangeEvent(originServerTs, sender, eventId, "m.room.canonical_alias", age, "canonical_alias", newAlias);
			if(isLatestStateEvent(event)) {
				canonicalAlias.setValue(content.get("alias").getAsString());
			}
		}
	}

	private void parseAliasesEvent(JsonObject content, String stateKey, int originServerTs, String sender, String eventId, int age) {
		if (content.has("aliases") &&
		    content.get("aliases").isJsonArray()) {

			JsonArray aliases = content.getAsJsonArray("aliases");

			ObservableList<String> list =
				FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

			for (JsonElement alias : aliases) {
				list.add(alias.getAsString());
			}

			Event event = addPropertyChangeEvent(originServerTs, sender, eventId, stateKey, age, "aliases", list);

			if (isLatestStateEvent(event)) {
				if (list.size() == 0){
					this.aliasLists.remove(stateKey);
				} else{
					this.aliasLists.put(stateKey, list);
				}
			}
		}
	}

	private void parseNameData(JsonObject data, int originServerTs, String sender, String eventId, int age) {
		if (data.has("name")) {
			String newName = data.get("name").getAsString();
			Event event = addPropertyChangeEvent(originServerTs, sender, eventId, "m.room.name", age, "name", newName);
			if(isLatestStateEvent(event)) {
				name.set(newName);
			}
		}
	}

	private void parseTopicData(JsonObject data, int originServerTs, String sender, String eventId, int age) {
		if (data.has("topic")) {
			String newTopic = data.get("topic").getAsString();
			Event event = addPropertyChangeEvent(originServerTs, sender, eventId, "m.room.topic", age, "topic", newTopic);
			if (isLatestStateEvent(event)) {
				topic.set(newTopic);
			}
		}
	}


	private void parseAvatarUrlData(JsonObject data, int originServerTs, String sender, String eventId, int age) {
		for(String key : new String[] {"avatar_url", "url"}) {
			if (data.has(key)) {
				try {
					URL newAvatarUrl = new URL(data.get(key).getAsString());


					Event event = addPropertyChangeEvent(originServerTs, sender, eventId, "m.room.avatar", age, "avatar", newAvatarUrl);

					if (isLatestStateEvent(event) &&
					   !newAvatarUrl.equals(avatarUrl.getValue())) {
						this.avatarUrl.set(newAvatarUrl);
						updateAvatar();
					}

				} catch(RestfulHTTPException | IOException e) {
					avatar.set(null);
					avatarUrl.set(null);
				}
				break;
			}
		}
	}

	private void updateAvatar() throws RestfulHTTPException, IOException{
		synchronized (avatarLock) {
			if (avatarWanted && avatarUrl.get() != null ) {
				if (!avatarUrl.get().equals(loadedAvatarUrl) || avatarSize > loadedAvatarSize){
					Image ava = ImageIO.read(api.getMediaContentThumbnail(avatarUrl.getValue(),avatarSize));
					loadedAvatarUrl = avatarUrl.get();
					loadedAvatarSize = avatarSize;
					avatar.set(ava);
				}
			} else {
				avatar.set(null);
			}
		}
	}

	private void parseMessageEvent(int originServerTs, String sender, String eventId, int age, JsonObject content) {
		if (!events.containsKey(eventId)){
			User author = userRepository.get(sender);
			this.events.put(
					eventId,
					new Message(api, originServerTs, author, eventId, age, content)
			);
		}
	}

	private void parseMemberEvent(JsonObject event, int originServerTs, String senderId, String eventId, int age, JsonObject content) {
		if (content.has("membership") &&
		    event.has("state_key")) {
			String memberId = event.get("state_key").getAsString();
			String membership = content.get("membership").getAsString();

			User sender = userRepository.get(senderId);
			MemberEvent memberEvent = new MemberEvent(api, originServerTs, sender, eventId, age, memberId, membership);

			if(isLatestStateEvent(memberEvent)) {
				User user = userRepository.get(memberId);
				user.update(event);

				Member member = new Member(user);

				if        ("join".equals(membership)) {
					if (!members.contains(member)) {
						members.add(member);
					}
				} else if ("leave".equals(membership)
				        || "ban".equals(membership)) {
					members.remove(member);
				} else if ("invite".equals(membership)) {
					// TODO: Handle invited room members in some way
				} else {
					System.out.printf("Unknown room membership type: \"%s\"%n", membership);
				}

				// Add membership event to the log
				if(!events.containsKey(eventId)) {
					events.put(eventId, memberEvent);
				}

				latestStateEvents.put(memberId, memberEvent);
			}
		}
	}

	private void parsePowerLevelsEvent(JsonObject data, int originServerTs, String senderId, String eventId, int age) {
		try {
			PermissionTable newTable = new PermissionTable(data);
			Event event = addPropertyChangeEvent(
					originServerTs,
					senderId,
					eventId,
					"m.room.power_levels",
					age,
					"power_levels",
					newTable
			);

			if (!isLatestStateEvent(event)) {
				return;
			}

			this.permissions.set(newTable);
		} catch(IOException e) {
			return;
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

	private boolean isLatestStateEvent(Event event) {
		String key;
		if(event instanceof MemberEvent) {
			key = ((MemberEvent)event).getUserId();
		} else {
			key = event.getType();
		}
		return !latestStateEvents.containsKey(key) ||
		       latestStateEvents.get(key).getOriginServerTs() <= event.getOriginServerTs();
	}

	private <T> Event addPropertyChangeEvent(int originServerTs, String senderId, String eventId, String eventType, int age, String property, T value) {
		if(!events.containsKey(eventId))
		{
			User sender = userRepository.get(senderId);
			PropertyChangeEvent<T> event = new PropertyChangeEvent<>(api, originServerTs, sender, eventId, eventType, age, property, value);
			events.put(eventId, event);
			if(isLatestStateEvent(event)) {
				latestStateEvents.put(eventType, event);
			}
			return event;
		}
		return events.get(eventId);
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
			} catch (RestfulHTTPException | IOException e) {
				System.out.printf("Failed to load image %s%n", e.getMessage());
			}

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
			} catch (RestfulHTTPException | IOException e) {
				System.out.printf("Failed to load image %s%n", e.getMessage());
			}
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
