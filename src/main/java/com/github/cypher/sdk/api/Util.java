package com.github.cypher.sdk.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/*
	Provides utils for the ApiLayer
 */
class Util {

	/*
		Build a URL with a specified set of parameters
	 */
	static URL UrlBuilder(String homeServer, Endpoint endPoint, Map<String, String> parameters) throws MalformedURLException {
		StringBuilder builder = new StringBuilder("https://");

		// Add homeserver and specified endpoint to the URL
		builder.append(homeServer);
		builder.append(endPoint);

		// Add Parameters
		if(parameters != null) {
			boolean first = true;
			for (Map.Entry<String, String> parameter : parameters.entrySet()) {
				builder.append(first ? "?" : "&");
				first = false;
				builder.append(parameter.getKey()).append("=");
				builder.append(parameter.getValue());
			}
		}

		// Format as URL and return
		return new URL(builder.toString());
	}

	static JsonElement makeJsonPostRequest(URL url, JsonObject data) throws RestfulHTTPException, IOException {
		// Setup the connection
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");

		// Push Data
		DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
		writer.writeBytes(data.toString());
		writer.flush();
		writer.close();

		// Retrieve Data
		JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(reader);

		// Throw exception if unsuccessful
		if(!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
			throw new RestfulHTTPException(conn.getResponseCode(), json.getAsJsonObject());
		}

		// Return response
		return json;
	}

	static JsonElement makeJsonGetRequest(URL url) throws RestfulHTTPException, IOException {

		// Setup connection
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		// Retrieve Data
		JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
		JsonParser parser = new JsonParser();
		JsonElement json = parser.parse(reader);

		// Throw exception if unsuccessful
		if(!(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)) {
			throw new RestfulHTTPException(conn.getResponseCode(), json.getAsJsonObject());
		}

		// Return response
		return json;
	}
}
