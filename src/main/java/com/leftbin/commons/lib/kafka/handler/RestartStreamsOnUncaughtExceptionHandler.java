package com.leftbin.commons.lib.kafka.handler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestartStreamsOnUncaughtExceptionHandler implements org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler {

    @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {
        log.info("uncaught exception in stream processing due to :{}", exception.getMessage());
        return StreamThreadExceptionResponse.REPLACE_THREAD;
    }
}
