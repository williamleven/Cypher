package com.github.cypher.model;

import com.github.cypher.DebugLogger;
import com.github.cypher.sdk.*;
import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;

public class Room {
	private final StringProperty id;
	private final StringProperty name;
	private final StringProperty topic;
	private final ObjectProperty<URL> avatarUrl;
	private final ObjectProperty<Image> avatar;
	private final StringProperty canonicalAlias;
	private final IntegerProperty memberCount;
	private final ObservableList<String> aliases;
	private final ObservableList<Event> events;
	private final com.github.cypher.sdk.Room sdkRoom;
	private final Client client;

	public Room(Client client, com.github.cypher.sdk.Room sdkRoom) {
		this.client = client;
		id = new SimpleStringProperty(sdkRoom.getId());
		name = new SimpleStringProperty(sdkRoom.getName());

		topic = new SimpleStringProperty(sdkRoom.getTopic());
		avatarUrl = new SimpleObjectProperty<>(sdkRoom.getAvatarUrl());
		avatar = new SimpleObjectProperty<>(null);
		updateAvatar(sdkRoom.getAvatar());
		canonicalAlias = new SimpleStringProperty(sdkRoom.getCanonicalAlias());
		memberCount = new SimpleIntegerProperty(sdkRoom.getMemberCount());
		aliases = FXCollections.synchronizedObservableList(FXCollections.observableArrayList(sdkRoom.getAliases()));
		events = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		this.sdkRoom = sdkRoom;

		sdkRoom.addNameListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> name.set(newValue));
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

		sdkRoom.addCanonicalAliasListener((observable, oldValue, newValue) -> {
			canonicalAlias.set(newValue);
		});

		sdkRoom.addMemberListener(change -> {
			memberCount.set(change.getMap().size());
		});

		sdkRoom.addAliasesListener((change -> {
			aliases.setAll(change.getList());
		}));

		sdkRoom.addEventListener((change -> {
			if (change.wasAdded()) {
				com.github.cypher.sdk.Event event = change.getValueAdded();
				if(event instanceof com.github.cypher.sdk.Message) {
					events.add(new Message(client, (com.github.cypher.sdk.Message)event));
				}
			}
		}));
	}

	public void sendMessage(String body) throws SdkException {
		try {
			sdkRoom.sendTextMessage(body);
		}catch(RestfulHTTPException | IOException ex){
			throw new SdkException(ex);
		}
	}

	private void updateAvatar(java.awt.Image image) {
		try {
			this.avatar.set(
					image == null ? null : com.github.cypher.Util.createImage(image)
			);
		} catch (IOException e) {
			if (DebugLogger.ENABLED) {
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

	public String getCanonicalAlias() {
		return canonicalAlias.get();
	}

	public StringProperty canonicalAliasProperty() {
		return canonicalAlias;
	}

	public int getMemberCount() {
		return memberCount.get();
	}

	public IntegerProperty memberCountProperty() {
		return memberCount;
	}

	public String[] getAliases() {
		return aliases.toArray(new String[aliases.size()]);
	}

	public ObservableList<String> aliasesList() {
		return aliases;
	}

	public ObservableList<Event> getEvents() {
		return events;
	}
}
