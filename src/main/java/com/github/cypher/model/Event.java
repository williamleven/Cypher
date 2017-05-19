package com.github.cypher.model;

public class Event {
	private final String eventId;
	private final long originServerTimesStamp;
	private final User sender;

	Event(Repository<User> repo,com.github.cypher.sdk.Event sdkEvent){
		eventId = sdkEvent.getEventId();
		originServerTimesStamp = sdkEvent.getOriginServerTs();
		sender = repo.get(sdkEvent.getSender().getId());
	}

	public String getEventId() {
		return eventId;
	}

	public long getOriginServerTimesStamp() {
		return originServerTimesStamp;
	}

	public User getSender() {
		return sender;
	}
}
