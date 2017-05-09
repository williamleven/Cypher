package com.github.cypher.sdk.api;

/*
	Describes the various Client-Server API Endpoints
 */
enum Endpoint {

	// All endpoints
	THIRD_PERSON_ID   ("account/3pid"),
	ROOM_CREATE       ("createRoom"),
	ROOM_DIRECTORY    ("directory/room/{0}"),      // {0} = roomAlias
	LOGIN             ("login"),
	TOKEN_REFRESH     ("tokenrefresh"),
	LOGOUT            ("logout"),
	SYNC              ("sync"),
	USER_PROFILE      ("profile/{0}"),             // {0} = userId
	USER_AVATAR_URL   ("profile/{0}/avatar_url"),  // {0} = userId
	USER_DISPLAY_NAME ("profile/{0}/displayname"), // {0} = userId
	PUBLIC_ROOMS      ("publicRooms"),
	ROOM_MESSAGES     ("rooms/{0}/messages"),      // {0} = roomId
	ROOM_MEMBERS      ("rooms/{0}/members"),       // {0} = roomId
	ROOM_SEND_EVENT   ("rooms/{0}/send/{1}/{2}"),  // {0} = roomId, {1} = eventType, {2} = txnId
	ROOM_JOIN         ("rooms/{0}/join"),          // {0} = roomId
	ROOM_LEAVE        ("rooms/{0}/leave"),         // {0} = roomId
	ROOM_KICK         ("rooms/{0}/kick"),          // {0} = roomId
	ROOM_INVITE       ("rooms/{0}/invite"),        // {0} = roomId
	PRESENCE_LIST     ("presence/list/{0}");       // {0} = userId

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