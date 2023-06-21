package com.leftbin.commons.lib.kafka.interceptors;

import com.leftbin.commons.lib.kafka.util.DebugData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

@Slf4j
public class ProducerInterceptor implements org.apache.kafka.clients.producer.ProducerInterceptor<Object, Object> {

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        log.info("producer record being sent out key: {}", DebugData.getDebugStringOfkafkaData(record.key()));
        log.debug("value: {}", DebugData.getDebugStringOfkafkaData(record.value()));
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.warn("exception encountered producing record {}", exception);
        } else {
            log.info("record has been acknowledged {} ", metadata.toString());
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
