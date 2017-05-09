package com.github.cypher.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
	private final Client client;
	private final User           author;
	private final StringProperty body;
	private final StringProperty formattedBody;

	public Message(Client client, com.github.cypher.sdk.Message sdkMessage) {
		this.client = client;
		this.author = client.getUser(sdkMessage.getSender().getId());
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

	public User getAuthor() {
		return author;
	}
}
