package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;

public class User {
	private final StringProperty        id;
	private final StringProperty        name;
	private final ObjectProperty<URL> avatarUrl;
	private final ObjectProperty<Image> avatar;

	User(com.github.cypher.sdk.User sdkUser) {
		this.id = new SimpleStringProperty(sdkUser.getId());
		this.name = new SimpleStringProperty(sdkUser.getName());
		this.avatarUrl = new SimpleObjectProperty<>(sdkUser.getAvatarUrl());
		this.avatar = new SimpleObjectProperty<>(null);
		updateAvatar(sdkUser.getAvatar());

		sdkUser.addNameListener((observable, oldValue, newValue) -> {
			name.set(newValue);
		});

		sdkUser.addAvatarUrlListener((observable, oldValue, newValue) -> {
			avatarUrl.set(newValue);
		});

		sdkUser.addAvatarListener((observable, oldValue, newValue) -> {
			updateAvatar(newValue);
		});
	}

	private void updateAvatar(java.awt.Image image) {
		try {
			this.avatar.set(
					image == null ? null : Util.createImage(image)
			);
		} catch(IOException e) {
			System.out.printf("IOException when converting user avatar image: %s\n", e);
		}
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

	public URL getAvatarUrl() {
		return avatarUrl.get();
	}

	public ObjectProperty<URL> avatarUrlProperty() {
		return avatarUrl;
	}

	public Image getAvatar() {
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		return avatar;
	}
}
