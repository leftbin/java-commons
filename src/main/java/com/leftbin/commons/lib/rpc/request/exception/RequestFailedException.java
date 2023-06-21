package com.leftbin.commons.lib.rpc.request.exception;

import java.util.Objects;

public class RequestFailedException extends RuntimeException {

    public RequestFailedException(String message) {
        super(message);
    }

    public RequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        if(Objects.isNull(this.getCause())) {
            return super.getMessage();
        }
        return String.format("%s with cause: %s", super.getMessage(), this.getCause().getMessage());
    }
}
