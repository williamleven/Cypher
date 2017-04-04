package com.github.cypher.sdk.api;

import com.github.cypher.DebugLogger;
import com.github.cypher.sdk.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

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

	private JsonObject login(String username, String password, String homeserver) {
		URL url;
		try {
			url = Util.UrlBuilder(homeserver, Endpoint.login, null);
		} catch (MalformedURLException e) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log(e.toString());
			}
			return null;
		}

		JsonObject request  = new JsonObject();
		request.addProperty("type", "m.login.password");
		request.addProperty("user", username);
		request.addProperty("password", password);

		try {
			return Util.makeJsonPostRequest(url, request).getAsJsonObject();
		} catch(IOException e) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log(e);
			}
		} catch(ExtendedHTTPException e) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log(e);
			}
		}
		return null;
	}

	public JsonObject sync(String filter, String since, boolean fullState, User.Presence setPresence) {
		URL url;
		try {
			Map<String, String> parameters = new HashMap<String, String>();

			if(filter != null && !filter.equals("")) parameters.put("filter"      , filter);
			if(since  != null && !since.equals(""))  parameters.put("since"       , since);
			if(setPresence != null)                 parameters.put("set_presence", setPresence.name());
			parameters.put("full_state"  , fullState ? "true" : "false");
			parameters.put("access_token", accessToken);

			url = Util.UrlBuilder(homeServer, Endpoint.sync, parameters);
		} catch(MalformedURLException e) {
			if(DebugLogger.ENABLED)
				DebugLogger.log(e);
			return null;
		}

		JsonElement response;
		try {
			response = Util.makeJsonGetRequest(url);
		} catch(IOException e) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log(e);
			}
			return null;
		} catch(ExtendedHTTPException e) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log(e);
			}
			return null;
		}

		out.println(response.toString());

		return response.getAsJsonObject();
	}
}
