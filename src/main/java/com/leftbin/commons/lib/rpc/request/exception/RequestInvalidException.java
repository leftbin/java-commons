package com.leftbin.commons.lib.rpc.request.exception;

public class RequestInvalidException extends RuntimeException {
    public RequestInvalidException(String preCondition) {
        super(preCondition);
    }
}
