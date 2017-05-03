package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Message {
	private StringProperty        body;
	private StringProperty        formattedBody;
	private StringProperty        author;
	private ObjectProperty<Image> avatar;

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

	public StringProperty getBody() { return body; }
	public StringProperty getFormattedBody() { return formattedBody; }
	public StringProperty getAuthor() { return author; }
	public ObjectProperty<Image> getAvatar() { return avatar; }
}
