package com.leftbin.commons.lib.kafka.exception;

public class KafkaProduceException extends Exception {
    public KafkaProduceException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public KafkaProduceException() {
        super("failed to produce event to kafka");
    }
}
