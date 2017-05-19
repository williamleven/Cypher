package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

public class Event {
	protected final ApiLayer api;

	private final long originServerTs;
	private final User sender;
	private final String eventId;
	private final int age;

	Event(ApiLayer api, long originServerTs, User sender, String eventId, int age) {
		this.api            = api;
		this.originServerTs = originServerTs;
		this.sender         = sender;
		this.eventId        = eventId;
		this.age            = age;
	}

	public long    getOriginServerTs(){ return originServerTs; }
	public User   getSender()         { return sender; }
	public String getEventId()        { return eventId; }
	public int    getAge()            { return age; }
}
