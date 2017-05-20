package com.github.cypher.model;

import com.github.cypher.sdk.api.RestfulHTTPException;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class Room {
	private final StringProperty id;
	private final StringProperty name;
	private final StringProperty topic;
	private final ObjectProperty<URL> avatarUrl;
	private final ObjectProperty<Image> avatar;
	private final StringProperty canonicalAlias;
	private final ObservableList<Member> members;
	private final IntegerProperty memberCount;
	private final ObservableList<String> aliases;
	private final ObservableList<Event> events;
	private final com.github.cypher.sdk.Room sdkRoom;
	private final User activeUser;

	private URL lastAvatarURL = null;

	Room(Repository<User> repo, com.github.cypher.sdk.Room sdkRoom, User activeUser) {
		this.sdkRoom = sdkRoom;
		this.activeUser = activeUser;

		id = new SimpleStringProperty(sdkRoom.getId());
		name = new SimpleStringProperty();

		topic = new SimpleStringProperty(sdkRoom.getTopic());
		avatarUrl = new SimpleObjectProperty<>(sdkRoom.getAvatarUrl());
		avatar = new SimpleObjectProperty<>();
		canonicalAlias = new SimpleStringProperty(sdkRoom.getCanonicalAlias());
		members = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
		for (com.github.cypher.sdk.Member sdkMember : sdkRoom.getMembers()) {
			members.add(new Member(sdkMember, repo));
		}

		memberCount = new SimpleIntegerProperty(sdkRoom.getMemberCount());
		aliases = FXCollections.synchronizedObservableList(FXCollections.observableArrayList(sdkRoom.getAliases()));
		events = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());


		updateName();
		updateAvatar();

		sdkRoom.addNameListener((observable, oldValue, newValue) -> {
			updateName();
			updateAvatar();
		});

		sdkRoom.addTopicListener((observable, oldValue, newValue) -> {
			topic.set(newValue);
		});

		sdkRoom.addAvatarUrlListener((observable, oldValue, newValue) -> {
			avatarUrl.set(newValue);
		});

		sdkRoom.addAvatarListener((observable, oldValue, newValue) -> {
			updateAvatar();
		});

		sdkRoom.addCanonicalAliasListener((observable, oldValue, newValue) -> {
			canonicalAlias.set(newValue);
		});

		sdkRoom.addMemberListener(change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					for (com.github.cypher.sdk.Member sdkMember : change.getAddedSubList()) {
						members.add(new Member(sdkMember, repo));
					}
				}
				if (change.wasRemoved()) {
					for (com.github.cypher.sdk.Member sdkMember : change.getRemoved()) {
						Optional<Member> optionalMember =  members.stream().filter(m -> sdkMember.getUser().getId().equals(m.getUser().getId())).findAny();
						members.remove(optionalMember.get());
					}
				}
			}
			memberCount.set(change.getList().size());
			updateName();
			updateAvatar();
		});

		sdkRoom.addAliasesListener((change -> {
			while (change.next()){
				if (change.wasAdded()){
					aliases.addAll(change.getAddedSubList());
				}
				if (change.wasRemoved()){
					aliases.removeAll(change.getRemoved());
				}
			}
		}));

		for (com.github.cypher.sdk.Event event :sdkRoom.getEvents().values()){
			if(event instanceof com.github.cypher.sdk.Message) {
				events.add(new Message(repo, (com.github.cypher.sdk.Message)event));
			}
		}
		sdkRoom.addEventListener((change -> {
			if (change.wasAdded()) {
				com.github.cypher.sdk.Event event = change.getValueAdded();
				if(event instanceof com.github.cypher.sdk.Message) {
					events.add(new Message(repo, (com.github.cypher.sdk.Message)event));
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

	private void updateAvatar() {
		java.awt.Image newImage = sdkRoom.getAvatar();
		if(newImage == null && isPmChat()){
			for (Member member: members) {
				if (member.getUser() != activeUser){
					if (lastAvatarURL == null || lastAvatarURL.equals(member.getUser().getAvatarUrl())){
						avatar.setValue(member.getUser().getAvatar());
						lastAvatarURL = member.getUser().getAvatarUrl();
					}
				}
			}
		}else{
			try {
				if (lastAvatarURL == null || lastAvatarURL.equals(sdkRoom.getAvatarUrl())){
					this.avatar.set(
						newImage == null ? null : Util.createImage(newImage)
					);
					lastAvatarURL = sdkRoom.getAvatarUrl();
				}
			} catch (IOException e) {
				System.out.printf("IOException when converting user avatar image: %s\n", e);
			}
		}

	}

	public boolean isPmChat() {
		boolean hasName = (sdkRoom.getName() != null && !sdkRoom.getName().isEmpty());
		return (this.getMemberCount() == 2 && !hasName);
	}

	public String getId() {
		return id.get();
	}

	public StringProperty idProperty() {
		return id;
	}

	public String getName() {
		return name.getValue();
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

	public ObservableList<Member> getMembersProperty() {
		return members;
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

	private void updateName(){
		String newName = sdkRoom.getName();
		if (isPmChat()){
			for (Member member: members) {
				if (member.getUser() != activeUser){
					name.setValue(member.getName().getValue());
				}
			}
		}else{
			name.setValue(newName);
		}
	}
}
