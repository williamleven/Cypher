package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;

public class Room {
	private final StringProperty        id;
	private final StringProperty        name;
	private final StringProperty        topic;
	private final ObjectProperty<URL>   avatarUrl;
	private final ObjectProperty<Image> avatar;

	public Room(com.github.cypher.sdk.Room sdkRoom) {
		id    = new SimpleStringProperty(sdkRoom.getId());
		name  = new SimpleStringProperty(sdkRoom.getName());
		topic = new SimpleStringProperty(sdkRoom.getTopic());
		avatarUrl = new SimpleObjectProperty<>(sdkRoom.getAvatarUrl());
		avatar    = new SimpleObjectProperty<>(null);
		updateAvatar(sdkRoom.getAvatar());

		sdkRoom.addNameListener((observable, oldValue, newValue) -> {
			name.set(newValue);
		});

		sdkRoom.addTopicListener((observable, oldValue, newValue) -> {
			topic.set(newValue);
		});

		sdkRoom.addAvatarUrlListener((observable, oldValue, newValue) -> {
			avatarUrl.set(newValue);
		});

		sdkRoom.addAvatarListener((observable, oldValue, newValue) -> {
			updateAvatar(newValue);
		});
	}

	private void updateAvatar(java.awt.Image image) {
		try {
			this.avatar.set(
				image == null ? null : com.github.cypher.Util.createImage(image)
			);
		} catch(IOException e) {
			if(DebugLogger.ENABLED) {
				DebugLogger.log("IOException when converting user avatar image: " + e);
			}
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

	public String getTopic() {
		return topic.get();
	}

	public StringProperty topicProperty() {
		return topic;
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
