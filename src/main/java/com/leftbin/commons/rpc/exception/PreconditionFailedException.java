package com.leftbin.commons.rpc.exception;

public class PreconditionFailedException extends Exception {
    public PreconditionFailedException(String precondition) {
        super(precondition);
    }
}
