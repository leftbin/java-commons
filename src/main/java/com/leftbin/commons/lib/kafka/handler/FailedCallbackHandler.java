package com.leftbin.commons.lib.kafka.handler;

import com.leftbin.commons.lib.kafka.exception.KafkaProduceException;
import org.springframework.util.concurrent.FailureCallback;

public class FailedCallbackHandler {
    public static FailureCallback produceFailCb() throws KafkaProduceException {
        throw new KafkaProduceException();
    }
}
