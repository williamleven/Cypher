package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Message {
	private final StringProperty        body;
	private final StringProperty        formattedBody;
	private final StringProperty        author;
	private final ObjectProperty<Image> avatar;

	public Message(String author, String body) {
		this.author = new SimpleStringProperty(author);
		this.body = new SimpleStringProperty(body);
		this.formattedBody = new SimpleStringProperty(null);
		this.avatar = new SimpleObjectProperty<>(null);
	}

	public Message(String author, String body, String formattedBody) {
		this.author = new SimpleStringProperty(author);
		this.body = new SimpleStringProperty(body);
		this.formattedBody = new SimpleStringProperty(formattedBody);
		this.avatar = new SimpleObjectProperty<>(null);
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

	public String getAuthor() {
		return author.get();
	}

	public StringProperty authorProperty() {
		return author;
	}

	public Image getAvatar() {
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		return avatar;
	}
}
