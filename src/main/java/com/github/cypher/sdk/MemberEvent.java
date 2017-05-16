package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

public class MemberEvent extends Event {
	final String userId;
	final String membership;

	MemberEvent(ApiLayer api, int originServerTs, User sender, String eventId, int age, String userId, String membership) {
		super(api, originServerTs, sender, eventId, age);
		this.userId = userId;
		this.membership = membership;
	}

	public String getUserId() {
		return userId;
	}

	public String getMembership() {
		return membership;
	}
}
