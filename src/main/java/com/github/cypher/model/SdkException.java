package com.github.cypher.model;

import com.github.cypher.sdk.api.RestfulHTTPException;

public class SdkException extends RuntimeException{

	private final Type type;
	private final Exception inner;

	enum Type {
		RATE_LIMIT,
		BAD_REQUEST,
		CONNECTION_TIMEOUT,
		AUTH,
		OTHER
	}

	SdkException(Exception ex){
		inner = ex;
		if (ex instanceof RestfulHTTPException){
			if (((RestfulHTTPException) ex).getErrorCode() != null &&
			    ((RestfulHTTPException) ex).getErrorCode().equals("M_UNKNOWN_TOKEN")){
				this.type = Type.AUTH;
			}else{
				this.type = Type.OTHER;
			}
		}else{
			this.type = Type.OTHER;
		}
	}

	public Type getType(){
		return this.type;
	}

	@Override
	public String getMessage(){
		return inner.getMessage();
	}
}
