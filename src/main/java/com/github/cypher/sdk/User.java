package com.github.cypher.sdk;

import com.github.cypher.DebugLogger;
import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a Matrix user
 * <p>User objects are returned by the methods of a Client or a Member object</p>
 */
public class User {
	public enum Presence{ONLINE, OFFLINE, UNAVAILABLE};

	protected final ApiLayer api;

	protected final String id;
	protected String name = null;
	protected URL avatarUrl = null;
	protected Image avatar = null;
	protected Presence presence = null;
	protected Boolean isActive = null;
	protected Long lastActiveAgo= null;

	User(ApiLayer api, String id) {
		this.api = api;
		this.id = id;
	}

	User(ApiLayer api, String id, String name, URL avatarUrl, Boolean isActive, Long lastActiveAgo) {
		this.api = api;
		this.id = id;
		this.name = name;
		this.avatarUrl = avatarUrl;
		this.isActive = isActive;
		this.lastActiveAgo = lastActiveAgo;
	}

	/**
	 * Get the user display name and avatar from the ApiLayer
	 * @see com.github.cypher.sdk.api.ApiLayer#getUserProfile(String)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void update() throws RestfulHTTPException, IOException {
		JsonObject profile = api.getUserProfile(id);

		if(profile.has("displayname")) {
			name = profile.get("displayname").getAsString();
		}

		try {
			if(profile.has("avatar_url")) {
				URL newAvatarUrl = new URL(profile.get("avatar_url").getAsString());
				if(!avatarUrl.equals(newAvatarUrl)) {
					// TODO: Get avatar image media
				}
				avatarUrl = newAvatarUrl;
			} else {
				avatarUrl = null;
				avatar = null;
			}
		} catch(MalformedURLException e) {
			DebugLogger.log(e);
		}
	}

	void update(JsonObject data) {
		if(data.has("type") &&
		   data.has("content")) {
			String type = data.get("type").getAsString();
			JsonObject contentObject = data.get("content").getAsJsonObject();
			if(type.equals("m.presence") &&
			   contentObject.has("presence")) {
				String presenceString = contentObject.get("presence").getAsString();
				presence = Presence.ONLINE;
				if(presenceString.equals("offline")) {
					presence = Presence.OFFLINE;
				} else if(presenceString.equals("unavailable")) {
					presence = Presence.UNAVAILABLE;
				}
			}
		}
	}

	public String getId() { return id; }
	public String getName() { return name; }
	public URL getAvatarUrl() { return avatarUrl; }
	public Image getAvatar() { return avatar; }
	public boolean getIsActive() { return isActive; }
	public long getLastActiveAgo() { return lastActiveAgo; }
}
