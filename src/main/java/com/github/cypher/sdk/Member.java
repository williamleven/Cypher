package com.github.cypher.sdk;

// Represents a member of a room
public class Member {
    private final User user;
    private final String displayname;
    private final String avatarUrl;
    private final String membership;

    public Member(User user, String displayname, String avatarUrl, String membership) {
        this.user = user;
        this.displayname = displayname;
        this.avatarUrl = avatarUrl;
        this.membership = membership;
    }

    public String getDisplayname() {
        return displayname;
    }

    public User getUser() {
        return new User(user);
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getMembership() {
        return membership;
    }
}
