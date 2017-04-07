package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RestfulHTTPExceptionTest {

	@Rule
	public ExpectedException expectedEc1 = ExpectedException.none();

	@Test
	public void ContructorWithOneJsonArgument(){
		// Build json
		JsonObject jObj = new JsonObject();
		jObj.addProperty("errcode", "some_Error_Code");

		// What to expect
		expectedEc1.expect(RestfulHTTPException.class);
		expectedEc1.expectMessage("some_Error_Code");
		expectedEc1.expectMessage("400");

		throw new RestfulHTTPException(400, jObj);

	}

	@Test
	public void ContructorWithTwoJsonArguments(){
		// Build json
		JsonObject jObj = new JsonObject();
		jObj.addProperty("errcode", "some_Error_Code");
		jObj.addProperty("error", "SomeOtherError");

		// What to Expect
		expectedEc1.expect(RestfulHTTPException.class);
		expectedEc1.expectMessage("400");
		expectedEc1.expectMessage("SomeOtherError");

		throw new RestfulHTTPException(400, jObj);

	}

	@Test
	public void ContructorWithNoJsonArguments(){
		// Build json
		JsonObject jObj = new JsonObject();

		// What to Expect
		expectedEc1.expect(RestfulHTTPException.class);
		expectedEc1.expectMessage("400");

		throw new RestfulHTTPException(400, jObj);

	}

}
