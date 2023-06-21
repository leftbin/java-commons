package com.leftbin.commons.lib.rpc.request.exception;

public class RoutingException extends Exception {

    /**
     * Constructs a new RoutingException with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public RoutingException(String message) {
        super(message);
    }

    /**
     * Constructs a new RoutingException with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *             (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public RoutingException(String message, Throwable cause) {
        super(message, cause);
    }
}
