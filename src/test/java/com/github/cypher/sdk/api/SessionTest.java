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
		jsonObject.addProperty("access_token", "555");
		jsonObject.addProperty("home_server", "matrix.org");

		// Make sure lacking object fails
		try {
			Session s = new Session(jsonObject);
			assertFalse("Parsing should fail without device_id", true);
		}catch (IOException err){
		}


		// Complete Object
		jsonObject.addProperty("device_id", "123");

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
