package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SessionTest {

	@Test
	public void constructorParsing(){

		// Crate lacking json object
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("user_id", "234");
		jsonObject.addProperty("home_server", "matrix.org");
		jsonObject.addProperty("refresh_token", "adj08aj9821321h9");
		jsonObject.addProperty("device_id", "123");

		// Make sure lacking object fails
		boolean passedCreation;
		try {
			new Session(jsonObject);
			passedCreation = true;
		}catch (IOException err){
			passedCreation = false;
		}
		assertFalse("Parsing should fail without access_token", passedCreation);

		// Complete Object
		jsonObject.addProperty("access_token", "555");

		// Make sure complete object works
		try{
			Session s = new Session(jsonObject);
			assertEquals("Access token should be 555", "555", s.getAccessToken());
			assertEquals("Device id should be 123", "123", s.getDeviceId());
			assertEquals("Homeserver should be matrix.org", "matrix.org", s.getHomeServer());
			assertEquals("User id should be 234", "234", s.getUserId());
		}catch (IOException err){
			assertFalse("Parsing should not fail with all properties set", true);
		}
	}
}
