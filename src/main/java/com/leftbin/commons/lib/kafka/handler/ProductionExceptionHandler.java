package com.leftbin.commons.lib.kafka.handler;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProductionExceptionHandler implements org.apache.kafka.streams.errors.ProductionExceptionHandler {
    private static final Logger l = LoggerFactory.getLogger(ProductionExceptionHandler.class);

    @Override
    public ProductionExceptionHandlerResponse handle(ProducerRecord<byte[], byte[]> record,
                                                     Exception exception) {

        if (exception instanceof RecordTooLargeException){
            l.info("unable to process event as record is too large err msg: {}, key: {}, value: {}", exception.getMessage(), record.key(), record.value());
            return ProductionExceptionHandlerResponse.CONTINUE;
        }
        l.info("unable to process event due to error: {}, key: {}, value: {}", exception.getMessage(), record.key(), record.value());
        return ProductionExceptionHandlerResponse.FAIL;
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
