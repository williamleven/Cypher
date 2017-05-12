package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

public class Event {
	protected final ApiLayer api;

	private final int originServerTs;
	private final User sender;
	private final String eventId;

	Event(ApiLayer api, int originServerTs, User sender, String eventId) {
		this.api = api;
		this.originServerTs = originServerTs;
		this.sender = sender;
		this.eventId = eventId;
	}

	public int getOriginServerTs() { return originServerTs; }
	public User getSender() { return sender; }
	public String getEventId() { return eventId; }
}
