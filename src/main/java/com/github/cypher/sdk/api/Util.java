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
import java.text.MessageFormat;
import java.util.Map;

/*
	Provides utils for the ApiLayer
 */
class Util {

	/*
		Build a URL with a specified set of parameters
	 */
	static URL UrlBuilder(String homeServer, Endpoint endPoint, Object[] endPointParameters, Map<String, String> getParameters) throws MalformedURLException {
		StringBuilder builder = new StringBuilder("https://");

		// Add homeserver and specified endpoint to the URL
		builder.append(homeServer);

		// Inject end point parameters into the URL
		if(endPointParameters == null) {
			builder.append(endPoint);
		} else {
			MessageFormat finalEndPoint = new MessageFormat(endPoint.toString());
			builder.append(finalEndPoint.format(endPointParameters));
		}

		// Append GET-parameters
		if(getParameters != null) {
			boolean first = true;
			for (Map.Entry<String, String> parameter : getParameters.entrySet()) {
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

		JsonElement json;
		try {
			// Retrieve Data
			JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
			json = new JsonParser().parse(reader);
		} catch(IOException e) {
			// Try to throw additional json error data
			JsonReader errorReader = new JsonReader(new InputStreamReader(conn.getErrorStream()));
			json = new JsonParser().parse(errorReader);
			throw new RestfulHTTPException(conn.getResponseCode(), json.getAsJsonObject());
		}

		// Return response
		return json;
	}

	static JsonElement makeJsonGetRequest(URL url) throws RestfulHTTPException, IOException {

		// Setup connection
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		JsonElement json;
		try {
			// Retrieve Data
			JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
			json = new JsonParser().parse(reader);
		} catch(IOException e) {
			// Try to throw additional json error data
			JsonReader errorReader = new JsonReader(new InputStreamReader(conn.getErrorStream()));
			json = new JsonParser().parse(errorReader);
			throw new RestfulHTTPException(conn.getResponseCode(), json.getAsJsonObject());
		}

		// Return response
		return json;
	}
}
