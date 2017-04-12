package com.github.cypher.sdk.api;

/*
	Describes the various Client-Server API Endpoints
 */
enum Endpoint {

	// All endpoints
	GET_ROOMID_FROM_ALIAS("directory/room/{0}"), // {0} = roomAlias
	LOGIN             ("login"),
	SYNC              ("sync"),
	USER_PROFILE      ("profile/{0}"),             // {0} = userId
	USER_AVATAR_URL   ("profile/{0}/avatar_url"),  // {0} = userId
	USER_DISPLAY_NAME ("profile/{0}/displayname"), // {0} = userId
	PUBLIC_ROOMS      ("publicRooms"),
	ROOM_MESSAGES     ("rooms/{0}/messages"),      // {0} = roomId
	ROOM_MEMBERS      ("rooms/{0}/members"),       // {0} = roomId
	ROOM_SEND_EVENT   ("rooms/{0}/send/{1}/{2}");  // {0} = roomId, {1} = eventType, {2} = txnId

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