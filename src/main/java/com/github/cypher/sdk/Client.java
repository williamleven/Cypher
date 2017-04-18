package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

// Poses as a chat instance, being the object returned from this package
public class Client {

    private ApiLayer api;

    public Client(ApiLayer api) {
        this.api = api;
    }
}
