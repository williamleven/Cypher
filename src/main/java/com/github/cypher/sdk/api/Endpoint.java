package com.github.cypher.sdk.api;

/*
	Describes the various Client-Server API Endpoints
 */
enum Endpoint {

	// All endpoints
	LOGIN        ("login"),
	SYNC         ("sync"),
	PUBLIC_ROOMS ("publicRooms");

	/*
		The code bellow allows for custom return of .toString() for each endpoint.
	 */
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