package com.leftbin.commons.lib.kafka.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DeserializationExceptionHandler implements org.apache.kafka.streams.errors.DeserializationExceptionHandler {
    private static final Logger l = LoggerFactory.getLogger(DeserializationExceptionHandler.class);

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext context, ConsumerRecord<byte[], byte[]> record,
                                                 Exception exception) {
        l.info("unable to process event due to error: {}, key: {}, value: {}", exception.getMessage(), record.key(), record.value());
        return DeserializationHandlerResponse.CONTINUE;
    }

    @Override
    public void configure(Map<String, ?> map) {
        //can read any property set in the stream here
    }
}
