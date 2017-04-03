package com.github.cypher.sdk.api;

import com.google.gson.JsonObject;

import javax.xml.ws.http.HTTPException;

public class ExtendedHTTPException extends HTTPException {

    final String message;
    final String errorCode;

    public ExtendedHTTPException(int statusCode) {
        super(statusCode);
        message = "";
        errorCode = "";
    }

    public ExtendedHTTPException(int statusCode, String errorCode) {
        super(statusCode);
        this.errorCode = errorCode;
        message = "";
    }

    public ExtendedHTTPException(int statusCode, String errorCode, String error) {
        super(statusCode);
        this.errorCode = errorCode;
        this.message = error;
    }

    public ExtendedHTTPException(int statusCode, JsonObject error){
        super(statusCode);

        if (error.has("error"))
            message = error.get("error").getAsString();
        else
            message = "";

        if (error.has("errcode"))
            errorCode = error.get("errcode").getAsString();
        else
            errorCode = "";
    }

    @Override
    public String getMessage() {
        if(!message.equals(""))
            return super.getMessage().concat("\n").concat(message);
        else if(errorCode.equals(""))
            return super.getMessage().concat("\n").concat(errorCode);
        else
            return super.getMessage();
    }
}
