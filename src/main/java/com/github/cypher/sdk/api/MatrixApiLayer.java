package com.github.cypher.sdk.api;

import com.github.cypher.DebugLogger;
import com.github.cypher.sdk.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

	public String getUserId() { return userId; }
	public String getAccessToken() { return accessToken; }
	public String getHomeServer() { return homeServer; }
	public String getDeviceId() { return deviceId; }

	private JsonObject login(String username, String password, String homeserver) throws ExtendedHTTPException, IOException {
		URL url = Util.UrlBuilder(homeserver, Endpoint.LOGIN, null);

		JsonObject request  = new JsonObject();
		request.addProperty("type", "m.login.password");
		request.addProperty("user", username);
		request.addProperty("password", password);

		return Util.makeJsonPostRequest(url, request).getAsJsonObject();
	}

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

	@Override
	public JsonObject publicRooms(String server) throws ExtendedHTTPException, IOException {
		URL url = Util.UrlBuilder(server, Endpoint.PUBLIC_ROOMS, null);
		return Util.makeJsonGetRequest(url).getAsJsonObject();
	}
}
