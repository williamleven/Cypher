package com.github.cypher.sdk.api;

/*
	Describes the various Client-Server API Endpoints
 */
enum Endpoint {

	LOGIN        ("login"),
	SYNC         ("sync"),
	PUBLIC_ROOMS ("publicRooms");

	private final String name;

	Endpoint(String s) {
		name = "/_matrix/client/r0/".concat(s);
	}

	public boolean equalsName(String otherName) {
		return name.equals(otherName);
	}

	@Override
	public String toString() {
		return this.name;
	}
}