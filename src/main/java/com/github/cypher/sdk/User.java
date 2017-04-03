package com.github.cypher.sdk;

// Represents a user
public class User {
    protected String id;
    protected String name;
    protected String url;
    protected boolean isActive;
    protected long lastActiveAgo;


    public User(User user) {
        id = user.getId();
        name = user.getName();
        url = user.getURl();
        isActive = user.getIsActive();
        lastActiveAgo = user.getLastActiveAgo();

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

    public String getURl() {
        return url;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public long getLastActiveAgo() {
        return lastActiveAgo;
    }
}
