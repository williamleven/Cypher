package com.github.cypher.sdk;

import com.github.cypher.sdk.api.RestfulHTTPException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class RoomTest {

	private ApiMock api = new ApiMock();
	private Room room = new Room(api, new Repository<User>((String id) -> {
		return new User(api, id);
	}), "!zion:matrix.org");

	@Test
	public void update() {
		JsonObject data = new JsonObject();
		data.addProperty("name", "testName");
		data.addProperty("topic", "testTopic");
		JsonObject timeline = new JsonObject();
		JsonArray events = new JsonArray();

		{ /* TEST MESSAGE EVENT */
			JsonObject event = new JsonObject();
			event.addProperty("type", "m.room.message");
			event.addProperty("origin_server_ts", 490328209);
			event.addProperty("sender", "@trinity:matrix.org");
			event.addProperty("event_id", "!sjkdkj2098sdf0:matrix.org");
			event.add("content", new JsonObject());
			events.add(event);
		}

		{ /* TEST MEMBER EVENT */
			JsonObject event = new JsonObject();
			event.addProperty("type", "m.room.member");
			event.addProperty("origin_server_ts", 9283578);
			event.addProperty("sender", "@morpheus:matrix.org");
			event.addProperty("event_id", "!ijxi12o924:matrix.org");
			event.addProperty("state_key", "@neo:matrix.org");

			JsonObject memberEventContent = new JsonObject();
			memberEventContent.addProperty("membership", "join");
			event.add("content", memberEventContent);

			events.add(event);
		}

		timeline.add("events", events);
		data.add("timeline", timeline);

		try {
			room.update(data);
		} catch(IOException e) {
			Assert.assertTrue("ApiMock should never throw an exception: " + e, false);
		}
		Assert.assertEquals("Room failed to process name", "testName", room.getName());
		Assert.assertEquals("Room failed to process topic", "testTopic", room.getTopic());

		Assert.assertNotNull(
			"Room failed to process member event",
			room.getMembers().get("@neo:matrix.org")
		);

		Assert.assertNotNull(
			"Room failed to process message event",
			room.getEvents().get("!sjkdkj2098sdf0:matrix.org")
		);
	}

	@Test
	public void sendTextMessage() throws RestfulHTTPException, IOException {
		api.textMessageSent = false;
		room.sendTextMessage("Down the rabbit hole");
		Assert.assertTrue("Room did not send text message event", api.textMessageSent);
	}
}
