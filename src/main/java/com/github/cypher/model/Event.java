package com.github.cypher.model;

public class Event {
	private final String eventId;
	private final long originServerTimeStamp;
	private final long timeStamp;
	private final User sender;

	Event(Repository<User> repo,com.github.cypher.sdk.Event sdkEvent){
		eventId = sdkEvent.getEventId();
		originServerTimeStamp = sdkEvent.getOriginServerTs();
		timeStamp = sdkEvent.getUnsignedTimeStamp();
		sender = repo.get(sdkEvent.getSender().getId());
	}

	public String getEventId() {
		return eventId;
	}

	public long getOriginServerTimeStamp() {
		return originServerTimeStamp;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public User getSender() {
		return sender;
	}
}
