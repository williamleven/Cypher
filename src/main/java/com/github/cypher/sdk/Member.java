package com.github.cypher.sdk;

// Represents a member of a room
public class Member {
    private final User user;
    private final String membership;

    public Member(User user, String displayname, String avatarUrl, String membership) {
        this.user = user;
        this.membership = membership;
    }



    public User getUser() {
        return new User(user);
    }


    public String getMembership() {
        return membership;
    }
}
