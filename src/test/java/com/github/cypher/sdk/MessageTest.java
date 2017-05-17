package com.github.cypher.sdk;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Test;

public class MessageTest {

	@Test
	public void createMessage() {
		// Build json
		JsonObject content = new JsonObject();
		content.addProperty("body", "TestBody");
		content.addProperty("msgtype", "TestType");

		// Create message
		Message m = new Message(null, 0, null, null, 0, content);

		// Make sure json was processed
		Assert.assertEquals("Message contructor failed to read body", "TestBody", m.getBody());
		Assert.assertEquals("Message contructor failed to read type", "TestType", m.getType());
	}
}
