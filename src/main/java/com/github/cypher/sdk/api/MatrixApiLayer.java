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
 */
public class MatrixApiLayer implements ApiLayer {

	private Session session;

	/**
	 * Creates a MatrixApiLayer with a session.
	 *
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough
	 */
	public MatrixApiLayer(String username, String password, String homeserver) throws ExtendedHTTPException, IOException {
		login(username, password, homeserver);
	}

	/**
	 * Crates a new MatrixApiLayer without a session.
	 *
	 * <p> Use {@link #login(String, String, String)} to create a session.
	 */
	public MatrixApiLayer() {}

	/**
	 * Authenticates the user.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Session_management/post_matrix_client_r0_login">matrix.org</a>
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough
	 */
	@Override
	public void login(String username, String password, String homeserver) throws ExtendedHTTPException, IOException {
		// Only run if session isn't already set
		if (session != null){
			return;
		}

		URL url = Util.UrlBuilder(homeserver, Endpoint.LOGIN, null);

		JsonObject request  = new JsonObject();
		request.addProperty("type", "m.login.password");
		request.addProperty("user", username);
		request.addProperty("password", password);

		JsonObject response = Util.makeJsonPostRequest(url, request).getAsJsonObject();

		this.session = new Session(response);
	}

	/**
	 * Synchronise the client's state and receive new messages.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_participation/get_matrix_client_r0_sync">matrix.org</a>
	 * @param filter Id of filter to be used on the sync data
	 * @param since Point in time of last sync request
	 * @param fullState Shall all events be collected
	 * @param setPresence User status
	 * @return Valid Json response
	 */
	@Override
	public JsonObject sync(String filter, String since, boolean fullState, User.Presence setPresence) throws ExtendedHTTPException, IOException{
		Map<String, String> parameters = new HashMap<String, String>();

		if(filter != null && !filter.equals("")) parameters.put("filter"      , filter);
		if(since  != null && !since.equals(""))  parameters.put("since"       , since);
		if(setPresence != null)                 parameters.put("set_presence", setPresence.name());
		parameters.put("full_state"  , fullState ? "true" : "false");
		parameters.put("access_token", session.getAccessToken());

		URL url = Util.UrlBuilder(session.getHomeServer(), Endpoint.SYNC, parameters);

		JsonElement response = Util.makeJsonGetRequest(url);
		return response.getAsJsonObject();
	}

	/**
	 * Lists the public rooms on the server.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Room_discovery/get_matrix_client_r0_publicRooms">matrix.org</a>
	 * @param server A homeserver to fetch public rooms from.
	 * @return Valid Json response
	 */
	@Override
	public JsonObject publicRooms(String server) throws ExtendedHTTPException, IOException {
		URL url = Util.UrlBuilder(server, Endpoint.PUBLIC_ROOMS, null);
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}
}
