package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

public class PropertyChangeEvent<T> extends Event {
	final String property;
	final T value;

	PropertyChangeEvent(ApiLayer api, int originServerTs, User sender, String eventId, int age, String property, T value) {
		super(api, originServerTs, sender, eventId, age);
		this.property = property;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public T getValue() {
		return value;
	}
}
