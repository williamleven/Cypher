package com.github.cypher.sdk;

import java.net.URL;

// Represents a user
public class User {
    public enum Presence{ONLINE, OFFLINE};

    protected final String id;
    protected String name;
    protected URL avatarUrl;
    protected boolean isActive;
    protected long lastActiveAgo;

    

    public User(String id, String name, URL avatarUrl, boolean isActive, long lastActiveAgo){
        this.id = id;
        this.name = name;
        this. avatarUrl= avatarUrl;
        this.isActive = isActive;
        this.lastActiveAgo = lastActiveAgo;
    }

    public void update(){
        //TODO
    }







    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public URL getAvatarUrl() {
        return avatarUrl;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public long getLastActiveAgo() {
        return lastActiveAgo;
    }
}
