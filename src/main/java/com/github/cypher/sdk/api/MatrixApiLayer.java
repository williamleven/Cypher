package com.github.cypher.sdk.api;

import com.github.cypher.sdk.User;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
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
	 * Crates a new MatrixApiLayer without a session.
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
	public JsonObject sync(String filter, String since, boolean fullState, User.Presence setPresence) throws RestfulHTTPException, IOException{

		// Build parameter Map
		Map<String, String> parameters = new HashMap<String, String>();
		if(filter != null && !filter.equals("")) {
			parameters.put("filter"      , filter);
		}
		if(since  != null && !since.equals("")) {
			parameters.put("since"       , since);
		}
		if(setPresence != null) {
			parameters.put("set_presence", setPresence.name());
		}
		parameters.put("full_state"  , fullState ? "true" : "false");
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
	public void setUserAvatarUrl(String avatarUrl) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.USER_AVATAR_URL, new Object[] {session.getUserId()}, parameters);

		// Build Json Object containing data
		JsonObject json = new JsonObject();
		json.addProperty("avatar_url", avatarUrl);

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

	@Override
	public JsonObject roomSendEvent(String roomId, String eventType, int transactionId, JsonObject content) throws RestfulHTTPException, IOException {
		// Build parameter Map
		Map<String, String> parameters = new HashMap<>();
		parameters.put("access_token", session.getAccessToken());

		// Build URL
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.ROOM_SEND_EVENT, new Object[] {roomId, eventType, transactionId}, parameters);

		// Send Request
		return Util.makeJsonPutRequest(url, content).getAsJsonObject();
	}

	@Override
	public JsonObject getRoomIDFromAlias(String roomAlias) throws RestfulHTTPException, IOException {
		//Build request URL.
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.GET_ROOMID_FROM_ALIAS,new Object[] {roomAlias}, null);

		//Send request URL.
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}
}
