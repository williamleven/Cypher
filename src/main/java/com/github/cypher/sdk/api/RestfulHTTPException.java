package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;

import javax.xml.ws.http.HTTPException;

/**
 * This class proves information rich errors usually returned from restful http API's
 */
public class RestfulHTTPException extends HTTPException {

	private final JsonObject errorResponse;

	/**
	 * Create error from a jsonObject
	 *
	 * @param statusCode HTTP status code
	 * @param errorResponse JsonObject containing error information
	 */
	public RestfulHTTPException(int statusCode, JsonObject errorResponse) {
		super(statusCode);
		this.errorResponse = errorResponse;
	}

	/**
	 * Crate and collect the restful error message.
	 */
	@Override
	public String getMessage() {
		String errorCode = getErrorCode();
		String errorMessage = errorResponse.has("error") ?
				errorResponse.get("error").getAsString() : "";

		if (!errorMessage.isEmpty()) {
			return Integer.toString(super.getStatusCode()).concat(": ").concat(errorMessage);
		} else if (!errorCode.isEmpty()) {
			return Integer.toString(super.getStatusCode()).concat(": ").concat(errorCode);
		} else {
			return Integer.toString(super.getStatusCode());
		}
	}

	/**
	 * Returns the errorcode
	 */
	public String getErrorCode(){
		if(errorResponse.has("errcode")) {
			return errorResponse.get("errcode").getAsString();
		}
		return "";
	}

	/**
	 * Returns the server request response
	 */
	public JsonObject getErrorResponse() {
		return errorResponse;
	}
}
