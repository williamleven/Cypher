package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

public class Event {
	protected final ApiLayer api;

	private final long originServerTs;
	private final User sender;
	private final String eventId;
	private final String type;
	private final int age;
	private final long unsignedTimeStamp;

	Event(ApiLayer api, long originServerTs, User sender, String eventId, String type, int age) {
		this.api            = api;
		this.originServerTs = originServerTs;
		this.sender         = sender;
		this.eventId        = eventId;
		this.type           = type;
		this.age            = age;
		unsignedTimeStamp = System.currentTimeMillis() - age;
	}

	public long   getOriginServerTs()   { return originServerTs; }
	public User   getSender()           { return sender; }
	public String getEventId()          { return eventId; }
	public String getType()             { return type; }
	public int    getAge()              { return age; }
	public long   getUnsignedTimeStamp(){ return unsignedTimeStamp; }
}
