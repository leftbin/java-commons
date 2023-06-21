package com.leftbin.commons.lib.db.exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public EntityNotFoundException(String name) {
        super(String.format("%s entity not found", name));
    }
}
