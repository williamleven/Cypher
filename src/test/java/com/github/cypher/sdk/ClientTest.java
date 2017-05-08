package com.github.cypher.sdk;

import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.Map;


public class ClientTest {

	ApiMock api = new ApiMock();
	Client client = new Client(api, "ex.example.test");

	// Test the user cache
	@Test
	public void getUser() {

		// Grab tha same user two times
		User u1 = client.getUser("test");
		client.getUser("some"); // Some disturbance
		User u2 = client.getUser("test");

		// Make sure the same object was returned both times
		Assert.assertSame(u1, u2);
	}


	@Test
	public void login() throws IOException {

		// Initiate resources
		api.loggedIn = false;

		// login
		client.login("user", "pass", "matrix.org");

		// Make sure sdk called login method
		Assert.assertTrue(api.loggedIn);

	}

	@Test
	public void update() throws IOException {
		client.update(1);

		// Make sure the correct settings vere loaded
		Assert.assertNull(
			"Client shouldn't read settings from another namespace",
			client.getSetting("other"));
		Assert.assertTrue(
			"client didn't read setting",
			Boolean.parseBoolean(client.getSetting("shouldBeTrue")));

		// Make sure rooms were loaded
		Assert.assertEquals("Didn't read correct amount of rooms", client.getJoinRooms().size(), 2);
		Assert.assertNotNull(client.getJoinRooms().keySet().contains("roomID1"));
		Assert.assertNotNull(client.getJoinRooms().keySet().contains("roomID2"));

		// Makes sure the SDK can handle since timestamps
		client.update(1);

		// Make sure setting was updated
		Assert.assertFalse(
			"client didn't overwrite setting",
			Boolean.parseBoolean(client.getSetting("shouldBeTrue")));

		// Make sure new rooms were added and old ones not removed
		Assert.assertEquals("Didn't read correct amount of rooms", client.getJoinRooms().size(), 4);
		Assert.assertNotNull(client.getJoinRooms().keySet().contains("roomID1"));
		Assert.assertNotNull(client.getJoinRooms().keySet().contains("roomID2"));
		Assert.assertNotNull(client.getJoinRooms().keySet().contains("roomID3"));
		Assert.assertNotNull(client.getJoinRooms().keySet().contains("roomID4"));

		// Todo: Test presence when it's implemented
		// Todo: Test all types of rooms when implemented
	}

	@Test
	public void getPublicRooms() throws IOException {

		// Collect rooms
		Map rooms = client.getPublicRooms("test");

		// Make sure they were collected properly
		Assert.assertTrue(rooms.size() == 2);
		Assert.assertNotNull(rooms.get("ID1"));
		Assert.assertNotNull(rooms.get("ID2"));
	}

}
