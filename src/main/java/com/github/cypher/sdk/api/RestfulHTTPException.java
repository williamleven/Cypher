package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;

import javax.xml.ws.http.HTTPException;

/**
 * This class proves information rich errors usually returned from restful http API's
 */
public class RestfulHTTPException extends HTTPException {

	private final String message;
	private final String errorCode;

	/**
	 * Create error without error code and error message.
	 * @param statusCode HTTP status code
	 */
	public RestfulHTTPException(int statusCode) {
		super(statusCode);
		message = "";
		errorCode = "";
	}

	/**
	 * Create error without error message.
	 * @param statusCode HTTP status code
	 * @param errorCode Json error code
	 */
	public RestfulHTTPException(int statusCode, String errorCode) {
		super(statusCode);
		this.errorCode = errorCode;
		message = "";
	}

	/**
	 * Create error with error message and error code
	 * @param statusCode HTTP status code
	 * @param errorCode Json error code
	 * @param error Message corresponding to the error code
	 */
	public RestfulHTTPException(int statusCode, String errorCode, String error) {
		super(statusCode);
		this.errorCode = errorCode;
		this.message = error;
	}

	/**
	 * Create error from a jsonObject
	 * @param statusCode HTTP status code
	 * @param error JsonObject containing error information
	 */
	public RestfulHTTPException(int statusCode, JsonObject error){
		super(statusCode);

		// Parse error message
		if (error.has("error")){
			message = error.get("error").getAsString();
		}else {
			message = "";
		}

		// Parse error code
		if (error.has("errcode")) {
			errorCode = error.get("errcode").getAsString();
		}else {
			errorCode = "";
		}
	}

	/**
	 * Crate and collect the error message.
	 */
	@Override
	public String getMessage() {
		if(!message.equals("")) {
			return super.getMessage().concat("\n").concat(message);
		}else if(errorCode.equals("")) {
			return super.getMessage().concat("\n").concat(errorCode);
		}else {
			return super.getMessage();
		}
	}
}
