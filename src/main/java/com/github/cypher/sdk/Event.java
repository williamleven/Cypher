package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

public class Event {
	protected final ApiLayer api;

	private final int originServerTs;
	private final String sender;
	private final String eventId;

	Event(ApiLayer api, int originServerTs, String sender, String eventId) {
		this.api = api;
		this.originServerTs = originServerTs;
		this.sender = sender;
		this.eventId = eventId;
	}

	public int getOriginServerTs() { return originServerTs; }
	public String getSender() { return sender; }
	public String getEventId() { return eventId; }
}
