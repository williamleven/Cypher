package com.github.cypher.sdk;

import com.github.cypher.sdk.api.ApiLayer;

import java.io.IOException;

// Poses as a chat instance, being the object returned from this package
public class Client {

    private ApiLayer api;

    public Client(ApiLayer api) throws IOException {
        this.api = api;
    }
}
