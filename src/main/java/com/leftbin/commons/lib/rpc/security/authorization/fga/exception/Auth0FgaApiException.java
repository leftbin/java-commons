package com.leftbin.commons.lib.rpc.security.authorization.fga.exception;

public class Auth0FgaApiException extends Exception {
    public Auth0FgaApiException(String msg) {
        super(msg);
    }

    public Auth0FgaApiException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
