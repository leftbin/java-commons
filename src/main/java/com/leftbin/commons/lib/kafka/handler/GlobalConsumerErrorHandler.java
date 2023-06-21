package com.leftbin.commons.lib.kafka.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;


@Slf4j
public class GlobalConsumerErrorHandler implements CommonErrorHandler {

    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        log.error("found unhandled exception while processing the record due to: {} for the consumer group: {}",
            thrownException.getMessage(), container.getGroupId(), thrownException);
    }
}
