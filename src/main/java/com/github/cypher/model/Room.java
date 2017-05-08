package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Room {
	private final StringProperty        id;
	private final StringProperty        name;
	private final StringProperty        topic;
	private final ObjectProperty<Image> avatar;

	public Room(String id, String name, String topic) {
		this.id    = new SimpleStringProperty(id);
		this.name  = new SimpleStringProperty(name);
		this.topic = new SimpleStringProperty(topic);
		this.avatar = new SimpleObjectProperty<>(null);
	}

	public String getId() {
		return id.get();
	}

	public StringProperty idProperty() {
		return id;
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getTopic() {
		return topic.get();
	}

	public StringProperty topicProperty() {
		return topic;
	}

	public Image getAvatar() {
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		return avatar;
	}
}
