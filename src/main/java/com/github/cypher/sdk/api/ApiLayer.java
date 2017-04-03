package com.github.cypher.sdk.api;

import com.github.cypher.sdk.User;
import com.google.gson.JsonObject;

public interface ApiLayer {
    JsonObject sync(String filter, String since, boolean fullState, User.Presence setPresence);
}
