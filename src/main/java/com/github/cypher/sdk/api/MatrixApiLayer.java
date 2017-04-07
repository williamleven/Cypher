package com.github.cypher.sdk.api;

import com.github.cypher.DebugLogger;
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
 * <p>Most endpoints require the {@link #login(String, String, String)} method to
 * have been successfully run as the data provided by the
 * endpoint itself isn't publicly available.
 */
public class MatrixApiLayer implements ApiLayer {

	private String userId;
	private String accessToken;
	private String homeServer;
	private String deviceId;

	public MatrixApiLayer(String username, String password, String homeserver) throws IOException {

		JsonObject response = login(username, password, homeserver);

		if(response.has("errcode")) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log(response.get("error").getAsString());
				DebugLogger.log(response.get("errcode").getAsString());
			}
			return;
		}

		this.userId = response.get("user_id").getAsString();
		this.accessToken = response.get("access_token").getAsString();
		this.homeServer = response.get("home_server").getAsString();
		this.deviceId = response.get("device_id").getAsString();
	}

	/**
	 * Authenticates the user.
	 * @see <a href="http://matrix.org/docs/api/client-server/#!/Session_management/post_matrix_client_r0_login">matrix.org</a>
	 * @param username Username
	 * @param password Password
	 * @param homeserver A homeserver to connect trough
	 * @return Valid Json response
	 */
	@Override
	public JsonObject login(String username, String password, String homeserver) throws ExtendedHTTPException, IOException {
		URL url = Util.UrlBuilder(homeserver, Endpoint.LOGIN, null);

		JsonObject request  = new JsonObject();
		request.addProperty("type", "m.login.password");
		request.addProperty("user", username);
		request.addProperty("password", password);

		return Util.makeJsonPostRequest(url, request).getAsJsonObject();
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
		parameters.put("access_token", accessToken);

		URL url = Util.UrlBuilder(homeServer, Endpoint.SYNC, parameters);

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
