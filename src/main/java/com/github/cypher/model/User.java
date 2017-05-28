package com.github.cypher.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;

public class User {
	private final StringProperty        id;
	private final StringProperty        name;
	private final ObjectProperty<URL> avatarUrl;
	private final ObjectProperty<Image> avatar;
	private boolean avatarWanted = false;
	private final com.github.cypher.sdk.User sdkUser;
	private final static int AVATAR_SIZE = 56;

	private ChangeListener avatarListener = (observable, oldValue, newValue) -> {
		updateAvatar();
	};

	User(com.github.cypher.sdk.User sdkUser) {
		this.sdkUser = sdkUser;
		this.id = new SimpleStringProperty(sdkUser.getId());
		this.name = new SimpleStringProperty(sdkUser.getName());
		this.avatarUrl = new SimpleObjectProperty<>(sdkUser.getAvatarUrl());
		this.avatar = new SimpleObjectProperty<>(null);

		sdkUser.addNameListener((observable, oldValue, newValue) -> {
			name.set(newValue);
		});

		sdkUser.addAvatarUrlListener((observable, oldValue, newValue) -> {
			avatarUrl.set(newValue);
		});
	}

	private void updateAvatar() {
		if (avatarWanted) {
			java.awt.Image image = sdkUser.getAvatar(AVATAR_SIZE);
			try {
				//56x56 is from Room avatar size. Shouldn't be hardcoded here!
				this.avatar.set(
					image == null ? Util.generateIdenticon(name.getValue() + id.getValue(), AVATAR_SIZE, AVATAR_SIZE) : Util.createImage(image)
				);
			} catch (IOException e) {
				System.out.printf("IOException when converting user avatar image: %s\n", e);
			}
		}
	}

	private void initiateAvatar(){
		if (!avatarWanted) {
			avatarWanted = true;
			updateAvatar();
			sdkUser.addAvatarListener(avatarListener, AVATAR_SIZE);
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
		initiateAvatar();
		return avatar.get();
	}

	public ObjectProperty<Image> avatarProperty() {
		initiateAvatar();
		return avatar;
	}
}
