package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;
import com.github.cypher.sdk.api.RestfulHTTPException;
import com.google.gson.JsonObject;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Represents a Matrix user
 * <p>User objects are returned by the methods of a Client or a Member object</p>
 */
public class User {
	public enum Presence{ONLINE, OFFLINE, UNAVAILABLE};

	protected final ApiLayer api;

	protected final String id;
	protected final StringProperty name               = new SimpleStringProperty(null);
	protected final ObjectProperty<URL> avatarUrl     = new SimpleObjectProperty<>(null);
	protected final ObjectProperty<Image> avatar      = new SimpleObjectProperty<>(null);
	private boolean avatarWanted = false;
	private final Object avatarLock = new Object();
	protected final ObjectProperty<Presence> presence = new SimpleObjectProperty<>(null);
	protected final BooleanProperty isActive          = new SimpleBooleanProperty(false);
	protected final LongProperty lastActiveAgo        = new SimpleLongProperty(0);
	private int avatarSize=24;

	private URL loadedAvatarUrl = null;
	private int loadedAvatarSize = 0;

	private final java.util.List<ChangeListener<? super Image>> avatarListeners = new ArrayList<>();

	User(ApiLayer api, String id) {
		this.api = api;
		this.id = id;
	}

	User(ApiLayer api, String id, String name, URL avatarUrl, Boolean isActive, Long lastActiveAgo) {
		this.api = api;
		this.id = id;
		this.name.set(name);
		this.avatarUrl.set(avatarUrl);
		this.isActive.set(isActive);
		this.lastActiveAgo.set(lastActiveAgo);
	}

	/**
	 * Get the user display name and avatar from the ApiLayer
	 * @see com.github.cypher.sdk.api.ApiLayer#getUserProfile(String)
	 * @throws RestfulHTTPException
	 * @throws IOException
	 */
	public void update() throws RestfulHTTPException, IOException {
		JsonObject profile = api.getUserProfile(id);
		if(profile.has("displayname") &&
		   profile.get("displayname").isJsonPrimitive()) {
			name.set(profile.get("displayname").getAsString());
		}

		//setAvatar(profile);
	}

	void update(JsonObject data) {
		if(data.has("type") &&
		   data.has("content")) {
			String type = data.get("type").getAsString();
			JsonObject contentObject = data.get("content").getAsJsonObject();

			// Parse presence data
			if("m.presence".equals(type) &&
			   contentObject.has("presence")) {
				String presenceString = contentObject.get("presence").getAsString();
				presence.set(Presence.ONLINE);
				if("offline".equals(presenceString)) {
					presence.set(Presence.OFFLINE);
				} else if("unavailable".equals(presenceString)) {
					presence.set(Presence.UNAVAILABLE);
				}
			}

			// Parse member data
			if ("m.room.member".equals(type)){
				if (contentObject.has("displayname") &&
				    contentObject.get("displayname").isJsonPrimitive()) {
					name.set(contentObject.get("displayname").getAsString());
				}
				if(avatar.getValue() == null) {
					setAvatar(contentObject);
				}
			}

		}

	}

	private void setAvatar(JsonObject contentObject) {
		if(contentObject.has("avatar_url") &&
		   contentObject.get("avatar_url").isJsonPrimitive()) {
			try {
				URL newAvatarUrl = new URL(contentObject.get("avatar_url").getAsString());
				if(!newAvatarUrl.equals(avatarUrl)) {
					avatarUrl.set(newAvatarUrl);
					updateAvatar();
				}
			} catch (RestfulHTTPException | IOException e) {
				avatar.set(null);
				avatarUrl.set(null);
			}
		} else {
			avatar.set(null);
			avatarUrl.set(null);
		}
	}

	private void updateAvatar() throws RestfulHTTPException, IOException{
		synchronized (avatarLock) {
			if (avatarWanted && avatarUrl.get() != null ) {
				if (!avatarUrl.get().equals(loadedAvatarUrl) || avatarSize > loadedAvatarSize){
					Image ava = ImageIO.read(api.getMediaContentThumbnail(avatarUrl.getValue(),avatarSize));
					loadedAvatarUrl = avatarUrl.get();
					loadedAvatarSize = avatarSize;
					avatar.set(ava);
				}
			} else {
				avatar.set(null);
			}
		}
	}


	public void addNameListener(ChangeListener<? super String> listener) {
		name.addListener(listener);
	}

	public void removeNameListener(ChangeListener<? super String> listener) {
		name.removeListener(listener);
	}

	public void addAvatarUrlListener(ChangeListener<? super URL> listener) {
		avatarUrl.addListener(listener);
	}

	public void removeAvatarUrlListener(ChangeListener<? super URL> listener) {
		avatarUrl.removeListener(listener);
	}

	public void addAvatarListener(ChangeListener<? super Image> listener,int size ) {
		synchronized (avatarLock) {
			avatarListeners.add(listener);
			avatarWanted = true;
			if (size > avatarSize) {
				avatarSize = size;
			}
			try {
				updateAvatar();
			} catch (RestfulHTTPException | IOException e) {
				System.out.printf("Failed to load image %s%n", e.getMessage());
			}

			avatar.addListener(listener);
		}
	}

	public void removeAvatarListener(ChangeListener<? super Image> listener) {
		synchronized (avatarLock) {
			avatarListeners.remove(listener);
			if (avatarListeners.isEmpty()) {
				avatarWanted = false;
			}
			avatar.removeListener(listener);
		}
	}

	public void addPresenceListener(ChangeListener<? super Presence> listener) {
		presence.addListener(listener);
	}

	public void removePresenceListener(ChangeListener<? super Presence> listener) {
		presence.removeListener(listener);
	}

	public void addIsActiveListener(ChangeListener<? super Boolean> listener) {
		isActive.addListener(listener);
	}

	public void removeIsActiveListener(ChangeListener<? super Boolean> listener) {
		isActive.removeListener(listener);
	}

	public void addLastActiveAgoListener(ChangeListener<? super Number> listener) {
		lastActiveAgo.addListener(listener);
	}

	public void removeLastActiveAgoListener(ChangeListener<? super Number> listener) {
		lastActiveAgo.removeListener(listener);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof User && id.equals(((User)o).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getId() { return id; }
	public String getName() { return name.get(); }
	public URL getAvatarUrl() { return avatarUrl.get(); }
	public Image getAvatar(int size) {
		synchronized (avatarLock) {
			boolean old = avatarWanted;
			avatarWanted = true;
			if (size > avatarSize) {
				avatarSize = size;
			}
			try {
				updateAvatar();
			} catch (RestfulHTTPException | IOException e) {
				System.out.printf("Failed to load image %s%n", e.getMessage());
			}
			avatarWanted = old;
			return avatar.get();
		}
	}
	public Presence getPresence() { return presence.get(); }
	public boolean getIsActive() { return isActive.get(); }
	public long getLastActiveAgo() { return lastActiveAgo.get(); }
}
