package com.lbn.commons.env.exception;

public class EnvVarMissingException extends Exception {
    public EnvVarMissingException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public EnvVarMissingException(String envVar) {
        super(String.format("required env var %s is missing", envVar));
    }
}
