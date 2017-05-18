package com.github.cypher.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message extends Event {
	private final Client client;
	private final StringProperty body;
	private final StringProperty formattedBody;

	Message(Client client, com.github.cypher.sdk.Message sdkMessage) {
		super(client, sdkMessage);
		this.client = client;
		this.body = new SimpleStringProperty(sdkMessage.getBody());
		this.formattedBody = new SimpleStringProperty(sdkMessage.getFormattedBody());

		sdkMessage.addBodyListener((observable, oldValue, newValue) -> {
			body.set(newValue);
		});

		sdkMessage.addFormattedBodyListener((observable, oldValue, newValue) -> {
			formattedBody.set(newValue);
		});
	}

	public String getBody() {
		return body.get();
	}

	public StringProperty bodyProperty() {
		return body;
	}

	public String getFormattedBody() {
		return formattedBody.get();
	}

	public StringProperty formattedBodyProperty() {
		return formattedBody;
	}
}
