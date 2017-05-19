package com.github.cypher.sdk.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides access to matrix endpoints trough
 * ordinary java methods returning Json Objects.
 *
 * <p>Most endpoints require a session to
 * have been successfully initiated as the data provided by the
 * endpoint itself isn't publicly available.
 *
 * <p>A session can be initiated with {@link #login(String username, String password, String homeserver)}
 * or via the constructor {@link #MatrixApiLayer(String username, String password, String homeserver)}
 *
 * @see <a href="http://matrix.org/docs/api/client-server/">matrix.org</a>
 */
public class MatrixApiLayer implements ApiLayer {

	private Session session;

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Creates a MatrixApiLayer with a session.
	 *
	 * <p> Session is created with {@link #login(String username, String password, String homeserver)}
	 *
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough (e.g. example.org:8448 or matrix.org)
	 */
	public MatrixApiLayer(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		login(username, password, homeserver);
	}

	/**
	 * Creates a new MatrixApiLayer without a session.
	 *
	 * <p> Use {@link #login(String username, String password, String homeserver)} to create a session.
	 */
	public MatrixApiLayer() {}

	@Override
	public void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		// Only run if session isn't already set
		if (session != null){
			return;
		}

		// Build URL
		URL url = Util.UrlBuilder(homeserver, Endpoint.LOGIN, null, null);

		// Build request body
		JsonObject request  = new JsonObject();
		request.addProperty("type", "m.login.password");
		request.addProperty("user", username);
		request.addProperty("password", password);

		// Send Request
		JsonObject response = Util.makeJsonPostRequest(url, request).getAsJsonObject();

		// Set Session
		this.session = new Session(response);
	}

	@Override
	public void refreshToken() throws RestfulHTTPException, IOException {
		// Only run if session is set
		if (session == null) {
			return;
		}

		// Only run if refreshToken is available
		if (session.getRefreshToken() == null) {
			throw new IOException("Refresh token not available");
		}

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.TOKEN_REFRESH, null, null);

		// Build request body
		JsonObject request = new JsonObject();
		request.addProperty("refresh_token", session.getRefreshToken());

		// Send Request
		JsonObject response = Util.makeJsonPostRequest(url, request).getAsJsonObject();

		// Check if response is valid
		if (response.has("access_token")) {

			// If refresh token is available, use it
			String refreshToken = null;
			if (response.has("refresh_token")) {
				refreshToken = response.get("refresh_token").getAsString();
			}

			// Create new session object
			session = new Session(
				session.getUserId(),
				response.get("access_token").getAsString(),
				refreshToken,
				session.getHomeServer(),
				session.getDeviceId(),
				0
			);
		} else {
			// Something went wrong, force re-login
			session = null;
		}
	}
  
	public void logout() throws RestfulHTTPException, IOException {
		// Only run if the session is set
		if (session == null) {
			return;
		}

		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.LOGOUT, null, parameters);

		// Send request
		JsonObject response = Util.makeJsonPostRequest(url, null).getAsJsonObject();

		// Null session
		this.session = null;
	}

	@Override
	public void register(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		// Only run if session isn't already set
		if (session != null){
			return;
		}

		// Build URL
		URL url = Util.UrlBuilder(homeserver, Endpoint.REGISTER, null, null);

		// Build request body
		JsonObject request  = new JsonObject();
		request.addProperty("password", password);
		if(username != null) {
			request.addProperty("username", username);
		}

		// Send Request
		JsonObject response = Util.makeJsonPostRequest(url, request).getAsJsonObject();

		// Set Session
		this.session = new Session(response);
	}


	@Override
	public JsonObject sync(String filter, String since, boolean fullState, Presence setPresence, int timeout) throws RestfulHTTPException, IOException{

		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		if(filter != null && !filter.equals("")) {
			parameters.put("filter", filter);
		}
		if(since  != null && !since.equals("")) {
			parameters.put("since", since);
		}
		if(setPresence != null) {
			parameters.put("set_presence", setPresence == Presence.ONLINE ? "online" : "offline");
		}
		if(fullState) {
			parameters.put("full_state", "true");
		}
		if(timeout > 0) {
			parameters.put("timeout", Integer.toString(timeout));
		}
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.SYNC, null, parameters);

		// Send request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public JsonObject getPublicRooms(String server) throws RestfulHTTPException, IOException {

		// Build URL
		URL url = Util.UrlBuilder(server, Endpoint.PUBLIC_ROOMS, null, null);

		// Send request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public JsonObject getRoomMessages(String roomId) throws RestfulHTTPException, IOException {

		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_MESSAGES, new Object[] {roomId}, parameters);

		// Send Request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public JsonObject getRoomMembers(String roomId) throws RestfulHTTPException, IOException {

		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_MEMBERS, new Object[] {roomId}, parameters);

		// Send Request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public JsonObject getUserProfile(String userId) throws RestfulHTTPException, IOException {
		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.USER_PROFILE, new Object[] {userId}, null);

		// Send Request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public JsonObject getUserAvatarUrl(String userId) throws RestfulHTTPException, IOException {
		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.USER_AVATAR_URL, new Object[] {userId}, null);

		// Send Request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public void setUserAvatarUrl(URL avatarUrl) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.USER_AVATAR_URL, new Object[] {session.getUserId()}, parameters);

		// Build Json Object containing data
		JsonObject json = new JsonObject();
		json.addProperty("avatar_url", avatarUrl.toString());

		// Send Request
		Util.makeJsonPutRequest(url, json);
	}

	@Override
	public JsonObject getUserDisplayName(String userId) throws RestfulHTTPException, IOException {
		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.USER_DISPLAY_NAME, new Object[] {userId}, null);

		// Send Request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public void setUserDisplayName(String displayName) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.USER_DISPLAY_NAME, new Object[] {session.getUserId()}, parameters);

		// Build Json Object containing data
		JsonObject json = new JsonObject();
		json.addProperty("displayname", displayName);

		// Send Request
		Util.makeJsonPutRequest(url, json);
	}
	//Bugged in current version of matrix, use with caution.
	@Override
	public JsonArray getUserPresence(String userId)throws RestfulHTTPException, IOException{
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.PRESENCE_LIST, new Object[] {userId}, parameters);

		// Send Request
		return Util.makeJsonGetRequest(url).getAsJsonArray();

	}

	@Override
	public JsonObject roomSendEvent(String roomId, String eventType, JsonObject content) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL, increment transactionId
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_SEND_EVENT, new Object[] {roomId, eventType, session.transactionId++}, parameters);


		// Send Request
		return Util.makeJsonPutRequest(url, content).getAsJsonObject();
	}

	@Override
	public JsonObject getRoomIDFromAlias(String roomAlias) throws RestfulHTTPException, IOException {
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_DIRECTORY,new Object[] {roomAlias}, null);

		//Send request URL.
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}
	@Override
	public JsonObject deleteRoomAlias(String roomAlias) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_DIRECTORY,new Object[] {roomAlias}, parameters);

		//Send request URL.
		return Util.makeJsonDeleteRequest(url, null).getAsJsonObject();
	}


	@Override
	public JsonObject putRoomAlias(String roomAlias, String roomId) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());


		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_DIRECTORY,new Object[] {roomAlias}, parameters);

		//Build JsonObject
		JsonObject roomIdJsonObject = new JsonObject();
		roomIdJsonObject.addProperty("room_id", roomId);

		//Send request URL.
		return Util.makeJsonPutRequest(url, roomIdJsonObject).getAsJsonObject();
	}

	@Override
	public JsonObject postCreateRoom(JsonObject roomCreation) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(),Endpoint.ROOM_CREATE,null, parameters);

		//Send request URL.
		return  Util.makeJsonPostRequest(url, roomCreation).getAsJsonObject();
	}
	@Override
	public JsonObject postJoinRoom(String roomId, JsonObject thirdPartySigned) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(),Endpoint.ROOM_JOIN,new Object[] {roomId}, parameters);

		//Send request URL.
		return  Util.makeJsonPostRequest(url, thirdPartySigned).getAsJsonObject();
	}
	@Override
	public void postLeaveRoom(String roomId) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(),Endpoint.ROOM_LEAVE, new Object[] {roomId}, parameters);

		//Send request URL.
		Util.makeJsonPostRequest(url, null);
	}
	@Override
	public void postKickFromRoom(String roomId, String reason, String userId) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(),Endpoint.ROOM_KICK, new Object[] {roomId}, parameters);

		//Build JsonObject
		JsonObject kick = new JsonObject();
		kick.addProperty("reason", reason);
		kick.addProperty("user_id",userId);
		//Send request URL.
		Util.makeJsonPostRequest(url, kick);
	}
	@Override
	public void postInviteToRoom(String roomId, String address, String idServer, String medium) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(),Endpoint.ROOM_INVITE, new Object[] {roomId}, parameters);

		//Build Json object
		JsonObject invite = new JsonObject();
		invite.addProperty("address", address);
		invite.addProperty("id_server", idServer);
		invite.addProperty("medium", medium);
		//Send request URL.
		Util.makeJsonPostRequest(url, invite);
	}
	@Override
	public void postInviteToRoom(String roomId, String userId/*contains "id_server","medium" and "address", or simply "user_id"*/) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(),Endpoint.ROOM_INVITE, new Object[] {roomId}, parameters);

		//Build JsonObject
		JsonObject invite = new JsonObject();
		invite.addProperty("user_id", userId);

		//Send request URL.
		Util.makeJsonPostRequest(url, invite);
	}
	@Override
	public JsonObject get3Pid() throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.THIRD_PERSON_ID,null, parameters);

		//Send request URL.
		return  Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public InputStream getMediaContent(URL mediaUrl) throws RestfulHTTPException, IOException {
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.MEDIA_DOWNLOAD, new Object[] {mediaUrl.getHost(),mediaUrl.getPath().replaceFirst("/", "")}, null);

		HttpURLConnection conn = null;
		try {
			// Setup the connection
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			return conn.getInputStream();
		} catch (IOException e) {
			Util.handleRestfulHTTPException(conn);
			return null;
		}
	}
	@Override
	public InputStream getMediaContentThumbnail(URL mediaUrl, int size) throws RestfulHTTPException, IOException {
		//Make parameter map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("height", String.valueOf(size));
		parameters.put("width", String.valueOf(size));



		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.MEDIA_THUMBNAIL, new Object[] {mediaUrl.getHost(),mediaUrl.getPath().replaceFirst("/", "")}, parameters);

		HttpURLConnection conn = null;
		try {
			// Setup the connection
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			return conn.getInputStream();
		} catch (IOException e) {
			Util.handleRestfulHTTPException(conn);
			return null;
		}
	}
}
