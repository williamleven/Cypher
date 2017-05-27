package com.github.cypher.sdk.api;

/*
	Describes the various Client-Server API Endpoints
 */
enum Endpoint {

	// All endpoints
	THIRD_PERSON_ID   ("client/r0/account/3pid"),
	ROOM_CREATE       ("client/r0/createRoom"),
	ROOM_DIRECTORY    ("client/r0/directory/room/{0}"),      // {0} = roomAlias
	LOGIN             ("client/r0/login"),
	TOKEN_REFRESH     ("client/r0/tokenrefresh"),
	LOGOUT            ("client/r0/logout"),
	REGISTER          ("client/r0/register"),
	SYNC              ("client/r0/sync"),
	USER_PROFILE      ("client/r0/profile/{0}"),             // {0} = userId
	USER_AVATAR_URL   ("client/r0/profile/{0}/avatar_url"),  // {0} = userId
	USER_DISPLAY_NAME ("client/r0/profile/{0}/displayname"), // {0} = userId
	PUBLIC_ROOMS      ("client/r0/publicRooms"),
	ROOM_MESSAGES     ("client/r0/rooms/{0}/messages"),      // {0} = roomId
	ROOM_MEMBERS      ("client/r0/rooms/{0}/members"),       // {0} = roomId
	ROOM_SEND_EVENT   ("client/r0/rooms/{0}/send/{1}/{2}"),  // {0} = roomId, {1} = ToggleEvent, {2} = txnId
	ROOM_JOIN         ("client/r0/rooms/{0}/join"),          // {0} = roomId
	ROOM_JOIN_ID_OR_A ("client/r0/join/{0}"),                // {0} = roomIdorAlias
	ROOM_LEAVE        ("client/r0/rooms/{0}/leave"),         // {0} = roomId
	ROOM_KICK         ("client/r0/rooms/{0}/kick"),          // {0} = roomId
	ROOM_INVITE       ("client/r0/rooms/{0}/invite"),        // {0} = roomId
	PRESENCE_LIST     ("client/r0/presence/list/{0}"),       // {0} = userId
	MEDIA_DOWNLOAD    ("media/r0/download/{0}/{1}"),         // {0} = serverName, {1} = mediaId
	MEDIA_THUMBNAIL   ("media/r0/thumbnail/{0}/{1}");        // {0} = serverName, {1} = mediaId

	/*
		The code bellow allows for custom return of .toString() for each endpoint.
	 */
	private final String name;

	Endpoint(String s) {
		name = "/_matrix/".concat(s);
	}

	public boolean equalsName(String otherName) {
		return name.equals(otherName);
	}

	@Override
	public String toString() {
		return this.name;
	}
}