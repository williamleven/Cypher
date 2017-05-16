package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiMock implements ApiLayer {

	boolean loggedIn = false;
	boolean textMessageSent = false;

	@Override
	public JsonObject sync(String filter, String since, boolean fullState, Presence setPresence, int timeout) throws RestfulHTTPException, IOException {
		JsonObject data = new JsonObject();

		// Emulate since token handeling
		if (since == null || since.isEmpty()) {
			data.addProperty("next_batch", "Test");
			data.add("presence", getPrecenceData());
			data.add("rooms", getRoomData());
			data.add("account_data", getAccountData());
		} else if (since.equals("Test")) {
			data.addProperty("next_batch", "");
			data.add("presence", getNewPrecenceData());
			data.add("rooms", getNewRoomData());
			data.add("account_data", getNewAccountData());
		}
		return data;
	}

	private JsonObject getPrecenceData() {
		JsonObject data = new JsonObject();
		// Todo: Not testable as you cant retrieve users from client yet
		return data;
	}

	private JsonObject getRoomData() {
		JsonObject data = new JsonObject();
		JsonObject join = new JsonObject();
		join.add("roomID1", new JsonObject());
		join.add("roomID2", new JsonObject());
		data.add("join", join);

		// TODO: "leave"-rooms (not implemented yet)
		// TODO: "invite"-rooms (not implemented yet)
		return data;
	}

	private JsonObject getAccountData() {
		JsonObject data = new JsonObject();
		JsonArray events = new JsonArray();

		{ // Dummy with other namespace
			JsonObject event = new JsonObject();
			JsonObject settings = new JsonObject();
			settings.addProperty("shouldBeTrue", true);

			event.addProperty("type", "ex.example.test");
			event.add("content", settings);
			events.add(event);
		}

		{ // Real one
			JsonObject event = new JsonObject();
			JsonObject settings = new JsonObject();
			settings.addProperty("other", true);

			event.addProperty("type", "ex.example.noTest");
			event.add("content", settings);
			events.add(event);
		}

		data.add("events", events);
		return data;
	}

	private JsonObject getNewPrecenceData() {
		JsonObject data = new JsonObject();
		// Todo: Not testable as you cant retrieve users from client yet
		return data;
	}

	private JsonObject getNewRoomData() {
		JsonObject data = new JsonObject();
		JsonObject join = new JsonObject();
		join.add("roomID2", new JsonObject());
		join.add("roomID3", new JsonObject());
		join.add("roomID4", new JsonObject());
		data.add("join", join);

		// TODO: "leave"-rooms
		// TODO: "invite"-rooms
		return data;
	}

	private JsonObject getNewAccountData() {
		JsonObject data = new JsonObject();
		JsonArray events = new JsonArray();

		JsonObject event = new JsonObject();
		JsonObject settings = new JsonObject();
		settings.addProperty("shouldBeTrue", false);

		event.addProperty("type", "ex.example.test");
		event.add("content", settings);
		events.add(event);

		data.add("events", events);
		return data;
	}

	@Override
	public JsonObject getPublicRooms(String server) throws RestfulHTTPException, IOException {
		JsonObject data = new JsonObject();
		JsonArray chunk = new JsonArray();

		JsonObject roomData = new JsonObject();
		roomData.addProperty("room_id", "ID1");
		chunk.add(roomData);

		JsonObject roomData2 = new JsonObject();
		roomData2.addProperty("room_id", "ID2");
		chunk.add(roomData2);

		data.add("chunk", chunk);
		return data;
	}

	@Override
	public void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		if (username.equals("user") && password.equals("pass") && homeserver.equals("matrix.org")) {
			loggedIn = true; // Record that it was called and untempered with
		}
	}

	@Override
	public JsonObject getRoomMessages(String roomId) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public JsonObject getRoomMembers(String roomId) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public JsonObject getUserProfile(String userId) throws RestfulHTTPException, IOException {
		JsonObject response = new JsonObject();
		response.addProperty("displayname", "Morpheus");
		return response;
	}

	@Override
	public JsonObject getUserAvatarUrl(String userId) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public void setUserAvatarUrl(URL avatarUrl) throws RestfulHTTPException, IOException {

	}

	@Override
	public JsonObject getUserDisplayName(String userId) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public void setUserDisplayName(String displayName) throws RestfulHTTPException, IOException {

	}

	@Override
	public JsonArray getUserPresence(String userId) throws IOException {
		return null;
	}

	@Override
	public JsonObject roomSendEvent(String roomId, String eventType, JsonObject content) throws RestfulHTTPException, IOException {

		if (roomId.equals("!zion:matrix.org") &&
		    eventType.equals("m.room.message") &&
		    content.has("body") &&
		    content.has("msgtype") &&
		    content.get("body").getAsString().equals("Down the rabbit hole") &&
		    content.get("msgtype").getAsString().equals("m.text")) {
			JsonObject response = new JsonObject();
			response.addProperty("event_id", "OISAJdiojd8s");
			textMessageSent = true;
			return response;
		}

		return null;
	}

	@Override
	public Session getSession() {
		return null;
	}

	@Override
	public void setSession(Session session) {

	}

	@Override
	public void logout() throws RestfulHTTPException, IOException {

	}

	@Override
	public void refreshToken() throws RestfulHTTPException, IOException {

	}

	@Override
	public JsonObject getRoomIDFromAlias(String roomAlias) throws MalformedURLException, IOException {
		return null;
	}

	@Override
	public JsonObject deleteRoomAlias(String roomAlias) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public JsonObject putRoomAlias(String roomAlias, String roomId) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public JsonObject postCreateRoom(JsonObject roomCreation) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public JsonObject postJoinRoom(String roomId, JsonObject thirdPartySigned) throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public void postLeaveRoom(String roomId) throws RestfulHTTPException, IOException {

	}

	@Override
	public void postKickFromRoom(String roomId, String reason, String userId) throws RestfulHTTPException, IOException {

	}

	@Override
	public void postInviteToRoom(String roomId, String address, String idServer, String medium) throws RestfulHTTPException, IOException {

	}

	@Override
	public void postInviteToRoom(String roomId, String userId) throws RestfulHTTPException, IOException {

	}

	@Override
	public JsonObject get3Pid() throws RestfulHTTPException, IOException {
		return null;
	}

	@Override
	public InputStream getMediaContent(URL mediaUrl) throws RestfulHTTPException, IOException {
		return null;
	}
}
