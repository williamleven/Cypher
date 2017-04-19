package com.github.cypher.sdk;

/**
 * Represents the membership of a User in a Room
 */
public class Member {
	private final User user;
	private int privilege = 0;

	Member(User user) {
		this.user = user;
	}

	Member(User user, int privilege) {
		this.user = user;
		this.privilege = privilege;
	}

	public User getUser() {
		return user;
	}


	public int getPrivilege() {
		return privilege;
	}
}
