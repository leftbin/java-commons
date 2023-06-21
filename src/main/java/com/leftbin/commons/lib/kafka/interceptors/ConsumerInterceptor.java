package com.leftbin.commons.lib.kafka.interceptors;

import com.leftbin.commons.lib.kafka.util.DebugData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Iterator;
import java.util.Map;

@Slf4j
public class ConsumerInterceptor implements org.apache.kafka.clients.consumer.ConsumerInterceptor<Object, Object> {
    public ConsumerInterceptor() {
        log.info("Built ConsumerInterceptor");
    }

    @Override
    public ConsumerRecords<Object, Object> onConsume(ConsumerRecords<Object, Object> consumerRecords) {
        log.info("Intercepted consumer records {}", buildMessage(consumerRecords.iterator()));
        return consumerRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {
        log.info("commit information: {}", map);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }

    private String buildMessage(Iterator<ConsumerRecord<Object, Object>> consumerRecords) {
        var builder = new StringBuilder();
        while (consumerRecords.hasNext()) {
            var consumerRecord = consumerRecords.next();
            builder.append(String.format("topic = %s, partition = %s, leaderEpoch = %s, offset = %s, CreateTime = %s" +
                            ", serialized key size = %s, serialized value size = %s, headers = %s, key = %s",
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.leaderEpoch(),
                    consumerRecord.offset(),
                    consumerRecord.timestamp(),
                    consumerRecord.serializedKeySize(),
                    consumerRecord.serializedValueSize(),
                    consumerRecord.headers(),
                    DebugData.getDebugStringOfkafkaData(consumerRecord.key())
            ));
        }
        return builder.toString();
    }
}
