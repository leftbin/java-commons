package com.leftbin.commons.lib.kafka.handler;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.List;

public class ConsumerErrorHandler implements ErrorHandler {
    private static final Logger l = LoggerFactory.getLogger(ConsumerErrorHandler.class);

    public ConsumerErrorHandler() {
    }

    @Override
    public void handle(Exception e, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
        if (e.getMessage().toLowerCase().contains("error deserializing key/value for partition")) {
            l.info("handling consumer exception with message listener container: {}", e.getMessage());
            var s = e.getMessage().split("Error deserializing key/value for partition ")[1].split(". If needed, please seek past the record to continue consumption.")[0];
            var topic = s.substring(0, s.lastIndexOf("-"));
            var offset = Integer.parseInt(s.split("offset ")[1]);
            var partition = Integer.parseInt(s.substring(s.lastIndexOf("-") + 1, s.lastIndexOf(" at")));

            TopicPartition topicPartition = new TopicPartition(topic, partition);
            l.info("skipping {}-{} offset {}", topic, partition, offset);
            consumer.seek(topicPartition, offset + 1);
            l.info("skipped {}-{} offset {}", topic, partition, offset);
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord) {
    }

    @Override
    public void handle(Exception e, ConsumerRecord<?, ?> consumerRecord, Consumer<?, ?> consumer) {
        if (e.getMessage().toLowerCase().contains("error deserializing key/value for partition")) {
            l.info("consumer exception : {}", e.getMessage());
            var s = e.getMessage().split("Error deserializing key/value for partition ")[1].split(". If needed, please seek past the record to continue consumption.")[0];
            var topic = s.substring(0, s.lastIndexOf("-"));
            var offset = Integer.parseInt(s.split("offset ")[1]);
            var partition = Integer.parseInt(s.substring(s.lastIndexOf("-") + 1, s.lastIndexOf(" at")));

            TopicPartition topicPartition = new TopicPartition(topic, partition);
            l.info("skipping {}-{} offset {}", topic, partition, offset);
            consumer.seek(topicPartition, offset + 1);
            l.info("skipped {}-{} offset {}", topic, partition, offset);
        } else {
            e.printStackTrace();
        }
    }
}
