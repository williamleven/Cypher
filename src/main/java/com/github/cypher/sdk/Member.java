package com.github.cypher.sdk;

// Represents a member of a room
public class Member {
    private final User user;
    private final int privilege;

    public Member(User user, int privilege) {
        this.user = user;
        this.privilege = privilege;
    }



    public User getUser() {
        return new User(user);
    }


    public int getMembership() {
        return privilege;
    }
}
