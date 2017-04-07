package com.github.cypher.sdk.api;

import com.github.cypher.sdk.User;
import com.google.gson.JsonObject;

import java.io.IOException;

public interface ApiLayer {
	JsonObject sync(String filter, String since, boolean fullState, User.Presence setPresence) throws ExtendedHTTPException, IOException;
	JsonObject publicRooms(String server) throws ExtendedHTTPException, IOException;
	JsonObject login(String username, String password, String homeserver) throws ExtendedHTTPException, IOException;
}
