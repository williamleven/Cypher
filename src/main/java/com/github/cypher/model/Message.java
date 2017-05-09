package com.github.cypher.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Message {
	private final User           author;
	private final StringProperty body;
	private final StringProperty formattedBody;

	public Message(User author, String body) {
		this.author = author;
		this.body = new SimpleStringProperty(body);
		this.formattedBody = new SimpleStringProperty(null);
	}

	public Message(User author, String body, String formattedBody) {
		this.author = author;
		this.body = new SimpleStringProperty(body);
		this.formattedBody = new SimpleStringProperty(formattedBody);
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
