package com.github.cypher.sdk.api;

import com.github.cypher.sdk.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
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
 * <p>A session can be initiated with {@link #login(String, String, String)}
 * or wia the constructor {@link #MatrixApiLayer(String, String, String)}
 *
 * @see <a href="http://matrix.org/docs/api/client-server/">matrix.org</a>
 */
public class MatrixApiLayer implements ApiLayer {

	private Session session;

	/**
	 * Creates a MatrixApiLayer with a session.
	 *
	 * <p> Session is created with {@link #login(String, String, String)}
	 *
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough
	 */
	public MatrixApiLayer(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		login(username, password, homeserver);
	}

	/**
	 * Crates a new MatrixApiLayer without a session.
	 *
	 * <p> Use {@link #login(String, String, String)} to create a session.
	 */
	public MatrixApiLayer() {}

	@Override
	public void login(String username, String password, String homeserver) throws RestfulHTTPException, IOException {
		// Only run if session isn't already set
		if (session != null){
			return;
		}

		// Build URL
		URL url = Util.UrlBuilder(homeserver, Endpoint.LOGIN, null);

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
		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.SYNC, parameters);

		// Send request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}

	@Override
	public JsonObject publicRooms(String server) throws RestfulHTTPException, IOException {

		// Build URL
		URL url = Util.UrlBuilder(server, Endpoint.PUBLIC_ROOMS, null);

		// Send request
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}
}
