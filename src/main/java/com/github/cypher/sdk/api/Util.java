package com.github.cypher.sdk.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.text.MessageFormat;
import java.util.Map;

/*
	Provides utils for the ApiLayer
 */
public final class Util {

	private Util() { /*Not constructable*/}

	/*
		Build a URL with a specified set of parameters
	 */
	static URL UrlBuilder(String homeServer, Endpoint endPoint, Object[] endPointParameters, Map<String, String> getParameters) throws MalformedURLException {
		StringBuilder builder = new StringBuilder("https://");

		try {
			// Add homeserver and specified endpoint to the URL
			builder.append(homeServer);

			// Inject end point parameters into the URL
			if(endPointParameters == null) {
				builder.append(endPoint);
			} else {
				Object[] formattedEndPointParameters = new Object[endPointParameters.length];
				for (int i = 0; i < endPointParameters.length; i++) {
					formattedEndPointParameters[i] = URLEncoder.encode(endPointParameters[i].toString(), "UTF-8");
				}
				MessageFormat finalEndPoint = new MessageFormat(endPoint.toString());
				builder.append(finalEndPoint.format(formattedEndPointParameters));
			}

			
			// Append GET-parameters
			if(getParameters != null) {
				boolean first = true;
				for (Map.Entry<String, String> parameter : getParameters.entrySet()) {
					builder.append(first ? "?" : "&");
					first = false;
					builder.append(URLEncoder.encode(parameter.getKey(), "UTF-8")).append("=");
					builder.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
				}
			}

			// Format as URL and return
			return new URL(builder.toString());
		} catch(UnsupportedEncodingException e) {
			System.out.println(builder.toString());
			System.out.println(e);
			return null;
		}
	}

	private static JsonElement makeRequest(URL url, String method, JsonObject data) throws RestfulHTTPException, IOException {
		// Setup the connection
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod(method);
		conn.setRequestProperty("Accept", "application/json");

		if(data != null) {
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");

			// Push Data
			DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
			writer.write(data.toString().getBytes("UTF-8"));
			writer.flush();
			writer.close();
		}

		JsonElement json = null;
		try {
			try {
				// Retrieve Data
				JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
				json = new JsonParser().parse(reader);
			} catch(IOException e) {
				handleRestfulHTTPException(conn);
			}
		} catch(IllegalStateException e) {
			throw new IOException(e.getMessage(), e.getCause());
		}

		// Return response
		return json;
	}

	static void handleRestfulHTTPException(HttpURLConnection conn) throws RestfulHTTPException, IOException {
		// Try to throw additional json error data
		if (conn.getErrorStream() == null){
			throw new IOException("Connection failed");
		}
		JsonReader errorReader = new JsonReader(new InputStreamReader(conn.getErrorStream()));
		JsonElement json = new JsonParser().parse(errorReader);
		throw new RestfulHTTPException(conn.getResponseCode(), json.getAsJsonObject());
	}

	static JsonElement makeJsonPostRequest(URL url, JsonObject data) throws RestfulHTTPException, IOException {
		return makeRequest(url, "POST", data);
	}

	static JsonElement makeJsonGetRequest(URL url) throws RestfulHTTPException, IOException {
		return makeRequest(url, "GET", null);
	}

	static JsonElement makeJsonPutRequest(URL url, JsonObject data) throws RestfulHTTPException, IOException {
		return makeRequest(url, "PUT", data);
	}
	static JsonElement makeJsonDeleteRequest(URL url, JsonObject data) throws RestfulHTTPException, IOException{
		return makeRequest(url,"DELETE", data);
	}

	/*
	  These classes allows for use of the mxc:// custom url schema used by the Matrix protocol
	*/
	public static class MatrixMediaURLStreamHandlerFactory implements URLStreamHandlerFactory {
		@Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
			if ("mxc".equals(protocol)) {
				return new MatrixMediaURLStreamHandler();
			}
			return null;
		}
	}
	public static class MatrixMediaURLStreamHandler extends URLStreamHandler {
		@Override
		protected URLConnection openConnection(URL url) throws IOException {
			return new MatrixMediaURLConnection(url);
		}
	}
	public static class MatrixMediaURLConnection extends URLConnection {
		protected MatrixMediaURLConnection(URL url) {
			super(url);
		}

		@Override
		public void connect() throws IOException {} // Matrix Media URLs can't be used to establish a connection.
	}
}
