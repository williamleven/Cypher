package com.github.cypher.sdk;

import com.github.cypher.DebugLogger;
import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableMapWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the metadata of a Matrix chat room,
 * as well as its messages and information about its members.
 *
 * <p>Room objects are returned by the methods of a Client object</p>
 * @see com.github.cypher.sdk.Client
 */
public class Room {
	private final ApiLayer api;
	private final Client client;

	private final String id;
	private String name = null;
	private String topic = null;
	private URL avatarUrl = null;
	private Image avatar = null;
	private ObservableMap<String, Event> events = new ObservableMapWrapper<>(new HashMap<>());
	private ObservableMap<String, Member> members = new ObservableMapWrapper<>(new HashMap<>());

	Room(ApiLayer api, Client client, String id) {
		this.api = api;
		this.client = client;
		this.id = id;
	}

	public void addEventListener(MapChangeListener<String, Event> listener) {
		events.addListener(listener);
	}

	public void removeEventListener(MapChangeListener<String, Event> listener) {
		events.removeListener(listener);
	}

	public void addMemberListener(MapChangeListener<String, Member> listener) {
		members.addListener(listener);
	}

	public void removeMemberListener(MapChangeListener<String, Member> listener) {
		members.removeListener(listener);
	}

	void update(JsonObject data) {
		parseNameData(data);

		parseTopicData(data);

		parseAvatarUrlData(data);

		parseTimelineData(data);
	}

	private void parseTimelineData(JsonObject data) {
		if(data.has("timeline") &&
		   data.get("timeline").isJsonObject()) {
			JsonObject timeline = data.get("timeline").getAsJsonObject();
			if(timeline.has("events") &&
			   timeline.get("events").isJsonArray()) {
				JsonArray events = timeline.get("events").getAsJsonArray();
				for(JsonElement eventElement : events) {
					if(eventElement.isJsonObject()) {
						parseTimelineEventData(eventElement.getAsJsonObject());
					}
				}
			}
		}
	}

	private void parseTimelineEventData(JsonObject event) {
		if(event.has("type") &&
		   event.has("origin_server_ts") &&
		   event.has("sender") &&
		   event.has("event_id") &&
		   event.has("content")) {

			int originServerTs = event.get("origin_server_ts").getAsInt();
			String sender = event.get("sender").getAsString();
			String eventId = event.get("event_id").getAsString();
			String eventType = event.get("type").getAsString();
			JsonObject content = event.get("content").getAsJsonObject();

			if(eventType.equals("m.room.message")) {
				parseMessageEvent(originServerTs, sender, eventId, content);
			} else if(eventType.equals("m.room.member")) {
				parseMemberEvent(event, content);
			}
		}
	}

	private void parseNameData(JsonObject data) {
		if(data.has("name")) {
			name = data.get("name").getAsString();
		}
	}

	private void parseTopicData(JsonObject data) {
		if(data.has("topic")) {
			topic = data.get("topic").getAsString();
		}
	}

	private void parseAvatarUrlData(JsonObject data) {
		if(data.has("avatar_url")) {
			try {
				URL newAvatarUrl = new URL(data.get("avatar_url").getAsString());
				if(!newAvatarUrl.equals(avatarUrl)) {
					// TODO: Get avatar image media
				}
			} catch(MalformedURLException e) {
				DebugLogger.log(e);
			}
		}
	}

	private void parseMessageEvent(int originServerTs, String sender, String eventId, JsonObject content) {
		User author = client.getUser(sender);
		this.events.put(
				eventId,
				new Message(api, originServerTs, author, eventId, content)
		);
	}

	private void parseMemberEvent(JsonObject event, JsonObject content) {
		if(content.has("membership") &&
		   event.has("state_key")) {
			String memberId = event.get("state_key").getAsString();
			String membership = content.get("membership").getAsString();

			User user = client.getUser(memberId);

			if (membership.equals("join")) {
				if (!members.containsKey(memberId)) {
					members.put(
							memberId,
							new Member(user)
					);
				}
			} else if (members.containsKey(memberId)) {
				members.remove(memberId);
			}
		}
	}

	/**
	 * Send a message event of the type "m.text" to this Matrix chat room
	 * @param message The message body
	 * @see com.github.cypher.sdk.api.ApiLayer#roomSendEvent(String, String, JsonObject)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void sendTextMessage(String message) throws RestfulHTTPException, IOException {
		JsonObject content = new JsonObject();
		content.addProperty("msgtype", "m.text");
		content.addProperty("body", message);
		sendMessage(content);
	}

	/**
	 * Send a custom message event to this Matrix chat room
	 * @param content The json object containing the message
	 * @see com.github.cypher.sdk.api.ApiLayer#roomSendEvent(String, String, JsonObject)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void sendMessage(JsonObject content) throws RestfulHTTPException, IOException {
		api.roomSendEvent(this.id, "m.room.message", content);
	}

	/**
	 * @return A valid room ID (e.g. "!cURbafjkfsMDVwdRDQ:matrix.org")
	 */
	public String getId() { return this.id; }
	public String getName() { return name; }
	public String getTopic() { return topic; }
	public Image getAvatar() { return avatar; }

	public Map<String, Member> getMembers() { return new HashMap<>(members); }
	public int getMemberCount() { return members.size(); }
}
