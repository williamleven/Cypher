package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;

import java.io.IOException;

/*
	Represents a User session
 */
class Session {

	private final String userId;
	private final String accessToken;
	private final String refreshToken;
	private final String homeServer;
	private final String deviceId;
	long transactionId = 0;

	/*
		Parses the data from a login response to create a session
	 */
	Session(JsonObject loginResponse) throws IOException{

		// Make sure response is valid
		if (loginResponse.has("user_id") &&
		    loginResponse.has("access_token") &&
		    loginResponse.has("home_server")){

			// Parse Data
			this.userId = loginResponse.get("user_id").getAsString();
			this.accessToken = loginResponse.get("access_token").getAsString();
			this.homeServer = loginResponse.get("home_server").getAsString();
		} else {
			throw new IOException("loginResponse wasn't parsable");
		}

		if(loginResponse.has("device_id")) {
			this.deviceId = loginResponse.get("device_id").getAsString();
		} else {
			this.deviceId = null;
		}

		if(loginResponse.has("refresh_token")) {
			this.refreshToken = loginResponse.get("refresh_token").getAsString();
		} else {
			this.refreshToken = null;
		}
	}

	public Session(
		String userId,
		String accessToken,
		String refreshToken,
		String homeServer,
		String deviceId,
		long transactionId
	) {
		this.userId = userId;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.homeServer = homeServer;
		this.deviceId = deviceId;
		this.transactionId = transactionId;
	}

	public String getUserId() {
		return userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getHomeServer() {
		return homeServer;
	}

	public String getDeviceId() {
		return deviceId;
	}
}
