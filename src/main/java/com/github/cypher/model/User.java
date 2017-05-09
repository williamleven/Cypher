package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class User {
	private final StringProperty        id;
	private final StringProperty        name;
	private final ObjectProperty<Image> avatar;

	public User(String id) {
		this.id = new SimpleStringProperty(id);
		this.name = new SimpleStringProperty(null);
		this.avatar = new SimpleObjectProperty<>(null);
	}

	public User(String id, String name) {
		this.id = new SimpleStringProperty(id);
		this.name = new SimpleStringProperty(name);
		this.avatar = new SimpleObjectProperty<>(null);
	}

	public User(String id, String name, Image avatar) {
		this.id = new SimpleStringProperty(id);
		this.name = new SimpleStringProperty(name);
		this.avatar = new SimpleObjectProperty<>(avatar);
	}

	public User(String id, Image avatar) {
		this.id = new SimpleStringProperty(id);
		this.name = new SimpleStringProperty(null);
		this.avatar = new SimpleObjectProperty<>(avatar);
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

	public Image getAvatar() {
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		return avatar;
	}
}
