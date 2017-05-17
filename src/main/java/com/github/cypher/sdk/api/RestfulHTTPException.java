package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;

import javax.xml.ws.http.HTTPException;

/**
 * This class proves information rich errors usually returned from restful http API's
 */
public class RestfulHTTPException extends HTTPException {

	private final String errorMessage;
	private final String errorCode;

	/**
	 * Create error without error code and error message.
	 *
	 * @param statusCode HTTP status code
	 */
	public RestfulHTTPException(int statusCode) {
		super(statusCode);
		errorMessage = "";
		errorCode = "";
	}

	/**
	 * Create error without error message.
	 *
	 * @param statusCode HTTP status code
	 * @param errorCode  Json error code
	 */
	public RestfulHTTPException(int statusCode, String errorCode) {
		super(statusCode);
		this.errorCode = errorCode;
		errorMessage = "";
	}

	/**
	 * Create error with error message and error code
	 *
	 * @param statusCode   HTTP status code
	 * @param errorCode    Json error code
	 * @param errorMessage Message corresponding to the error code
	 */
	public RestfulHTTPException(int statusCode, String errorCode, String errorMessage) {
		super(statusCode);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Create error from a jsonObject
	 *
	 * @param statusCode HTTP status code
	 * @param error      JsonObject containing error information
	 */
	public RestfulHTTPException(int statusCode, JsonObject error) {
		super(statusCode);

		// Parse error message
		if (error.has("error")) {
			errorMessage = error.get("error").getAsString();
		} else {
			errorMessage = "";
		}

		// Parse error code
		if (error.has("errcode")) {
			errorCode = error.get("errcode").getAsString();
		} else {
			errorCode = "";
		}
	}

	/**
	 * Crate and collect the restful error message.
	 */
	@Override
	public String getMessage() {
		if (!errorMessage.equals("")) {
			return Integer.toString(super.getStatusCode()).concat(": ").concat(errorMessage);
		} else if (!errorCode.equals("")) {
			return Integer.toString(super.getStatusCode()).concat(": ").concat(errorCode);
		} else {
			return Integer.toString(super.getStatusCode());
		}
	}

	/**
	 * Returns the errorcode
	 */
	public String getErrorCode(){
		return this.errorCode;
	}
}
