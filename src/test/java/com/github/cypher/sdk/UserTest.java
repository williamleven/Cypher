package com.github.cypher.sdk;

import com.github.cypher.sdk.api.RestfulHTTPException;
import com.github.cypher.sdk.api.Util;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

public class UserTest {
	private final ApiMock api = new ApiMock();
	private final User user = new User(api, "@morpheus:matrix.org");

	@Test
	public void update() throws RestfulHTTPException, IOException {
		// Required to make URL class accept mxc:// protocol
		URL.setURLStreamHandlerFactory(new Util.MatrixMediaURLStreamHandlerFactory());

		user.update();
		Assert.assertEquals(
			"User object did not parse display name",
			user.getName(),
			"Morpheus"
		);
	}

	@Test
	public void updateFromSync() {
		JsonObject data = new JsonObject();

		data.addProperty("type", "m.presence");

		JsonObject content = new JsonObject();
		content.addProperty("presence", "offline");
		data.add("content", content);

		user.update(data);

		Assert.assertEquals("User object did not parse presence sync data", user.getPresence(), User.Presence.OFFLINE);
	}
}
