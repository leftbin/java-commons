package com.leftbin.commons.lib.db.exception;

public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(String name) {
        super(String.format("%s entity already exists", name));
    }
}
