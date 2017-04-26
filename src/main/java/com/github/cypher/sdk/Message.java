package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;
import com.google.gson.JsonObject;

public class Message extends Event {
	private String body = "";
	private String type = "";

	Message(ApiLayer api, int originServerTs, String sender, String eventId, JsonObject content) {
		super(api, originServerTs, sender, eventId);
		if(content.has("body")) {
			this.body = content.get("body").getAsString();
		}
		if(content.has("msgtype")) {
			this.type = content.get("msgtype").getAsString();
		}
	}

	public String getBody() { return body; }
	public String getType() { return type; }
}
