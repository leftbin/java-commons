package com.leftbin.commons.lib.cloud.gcp.gcs.exception;

public class GcsObjectNotFoundException  extends Exception {
    public GcsObjectNotFoundException() {
        super();
    }

    public GcsObjectNotFoundException(String message) {
        super(message);
    }
}
