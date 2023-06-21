package com.leftbin.commons.lib.kafka.config;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDefaultProducerFactory {

    private final KafkaCommonConfig kafkaCommonConfig;


    public KafkaDefaultProducerFactory(KafkaCommonConfig kafkaCommonConfig) {
        this.kafkaCommonConfig = kafkaCommonConfig;
    }

    public <K, V> KafkaTemplate<K, V> defaultKafkaTemplate(K key, V value) {
        Map<String, Object> props = new HashMap<>(kafkaCommonConfig.getConsumerProducerConfig());
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.leftbin.commons.lib.kafka.interceptors.ProducerInterceptor");
        return new KafkaTemplate<K, V>(new DefaultKafkaProducerFactory<>(props));
    }
}
