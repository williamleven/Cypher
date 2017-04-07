package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;

import java.io.IOException;

/*
	Represents a User session
 */
class Session {

	private String userId;
	private String accessToken;
	private String homeServer;
	private String deviceId;

	/*
		Parses the data from a login response to create a session
	 */
	Session(JsonObject loginResponse) throws IOException{

		// Make sure response is valid
		if (loginResponse.has("user_id") &&
		    loginResponse.has("access_token") &&
		    loginResponse.has("home_server") &&
		    loginResponse.has("device_id")){

			// Parse Data
			this.userId = loginResponse.get("user_id").getAsString();
			this.accessToken = loginResponse.get("access_token").getAsString();
			this.homeServer = loginResponse.get("home_server").getAsString();
			this.deviceId = loginResponse.get("device_id").getAsString();

		}else {
			throw new IOException("loginResponse wasn't parsable");
		}

	}

	public String getUserId() {
		return userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getHomeServer() {
		return homeServer;
	}

	public String getDeviceId() {
		return deviceId;
	}
}
