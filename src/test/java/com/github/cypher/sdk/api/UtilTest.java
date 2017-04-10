package com.github.cypher.sdk.api;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UtilTest {


	@Test
	public void UrlBuilder() {

		// Different scenarios
		List<Map<String, String>> maps = new LinkedList<>();
		Map<String, String> map;

		map = new HashMap<>(1);
		map.put("av", "23");
		maps.add(map);

		map = new HashMap<>(2);
		map.put("av", "23");
		map.put("ab", "true");
		maps.add(map);

		try{

			assertEquals(
				"UrlBuilder must build correct URL with one argument",
				"https://matrix.org/_matrix/client/r0/login?av=23",
				Util.UrlBuilder("matrix.org", Endpoint.LOGIN, null, maps.get(0)).toString()
			);

			assertEquals(
				"UrlBuilder must build correct URL with no arguments",
				"https://matrix.org/_matrix/client/r0/login",
				Util.UrlBuilder("matrix.org", Endpoint.LOGIN, null, null).toString()
			);

			assertEquals(
				"UrlBuilder must build correct URL with no arguments",
				"https://matrix.org/_matrix/client/r0/rooms/!cURbafjkfsMDVwdRDQ:matrix.org/messages",
				Util.UrlBuilder("matrix.org", Endpoint.ROOM_MESSAGES, new Object[] {"!cURbafjkfsMDVwdRDQ:matrix.org"}, null).toString()
			);

			assertEquals(
				"UrlBuilder must build correct URL with multiple arguments",
				"https://matrix.org/_matrix/client/r0/rooms/!cURbafjkfsMDVwdRDQ:matrix.org/messages?av=23&ab=true",
				Util.UrlBuilder("matrix.org", Endpoint.ROOM_MESSAGES, new Object[] {"!cURbafjkfsMDVwdRDQ:matrix.org"}, maps.get(1)).toString()
			);

		}catch (MalformedURLException e) {
			assertFalse(e.getMessage(), true);
		}
	}
}
