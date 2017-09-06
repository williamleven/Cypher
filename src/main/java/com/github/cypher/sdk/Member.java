package com.github.cypher.sdk;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;

/**
 * Represents the membership of a User in a Room
 */
public class Member {
	private final User user;
	private final IntegerProperty privilege;

	Member(User user) {
		this.user = user;
		this.privilege = new SimpleIntegerProperty(0);
	}

	Member(User user, int privilege) {
		this.user = user;
		this.privilege = new SimpleIntegerProperty(privilege);
	}

	public User getUser() {
		return user;
	}

	public void addPrivilegeListener(ChangeListener<? super Number> listener) {
		privilege.addListener(listener);
	}

	public void removePrivilegeListener(ChangeListener<? super Number> listener) {
		privilege.removeListener(listener);
	}

	public int getPrivilege() {
		return privilege.get();
	}

	IntegerProperty privilegeProperty() {
		return privilege;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Member && user.equals(((Member)o).user);
	}

	@Override
	public int hashCode() {
		return user.hashCode();
	}
}
