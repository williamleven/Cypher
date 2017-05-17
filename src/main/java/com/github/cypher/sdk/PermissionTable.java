package com.github.cypher.sdk;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PermissionTable {
	private final int sendEvents;
	private final int invite;
	private final int setState;
	private final int redact;
	private final int ban;
	private final int defaultPower;
	private final int kick;
	private final Map<String, Integer> specialEvents;

	public PermissionTable(int sendEvents, int invite, int setState, int redact, int ban, int defaultPower, int kick, Map<String, Integer> specialEvents) {
		this.sendEvents = sendEvents;
		this.invite = invite;
		this.setState = setState;
		this.redact = redact;
		this.ban = ban;
		this.defaultPower = defaultPower;
		this.kick = kick;
		this.specialEvents = new HashMap<String, Integer>(specialEvents);
	}

	public PermissionTable(JsonObject data) throws IOException {
		if(data.has("events_default") &&
		   data.has("invite"        ) &&
		   data.has("state_default" ) &&
		   data.has("redact"        ) &&
		   data.has("ban"           ) &&
		   data.has("users_default" ) &&
		   data.has("kick"          )) {

			sendEvents   = data.get("events_default").getAsInt();
			invite       = data.get("invite"        ).getAsInt();
			setState     = data.get("state_default" ).getAsInt();
			redact       = data.get("redact"        ).getAsInt();
			ban          = data.get("ban"           ).getAsInt();
			defaultPower = data.get("users_default" ).getAsInt();
			kick         = data.get("kick"          ).getAsInt();
		} else {
			throw new IOException("Invalid PermissionTable json data: " + data.toString());
		}

		specialEvents = new HashMap<>(0);
		if(data.has("events") &&
		   data.get("events").isJsonObject()) {

			for(Map.Entry<String, JsonElement> entry : data.get("events").getAsJsonObject().entrySet()) {
				specialEvents.put(
						entry.getKey(),
						entry.getValue().getAsInt()
				);
			}
		}
	}


	public int getSendEvents() {
		return sendEvents;
	}

	public int getInvite() {
		return invite;
	}


	public int getSetState() {
		return setState;
	}


	public int getRedact() {
		return redact;
	}


	public int getBan() {
		return ban;
	}


	public int getDefaultPower() {
		return defaultPower;
	}


	public int getKick() {
		return kick;
	}


	public Map<String, Integer> getSpecialEvents() {

		return new HashMap<String, Integer>(specialEvents);
	}


}
