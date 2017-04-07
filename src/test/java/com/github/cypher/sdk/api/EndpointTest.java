package com.github.cypher.sdk.api;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class EndpointTest {

	@Test
	public void EndpointValues(){

		// Make sure all endpoint build correct URL's
		try {
			for(Endpoint endpoint : Endpoint.values()){
				new URL("https://matrix.org".concat(endpoint.toString()));
			}
		}catch (MalformedURLException e){
			assertFalse(e.getMessage(), true);
		}
	}
}
