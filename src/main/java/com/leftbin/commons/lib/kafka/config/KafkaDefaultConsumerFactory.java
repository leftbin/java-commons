package com.leftbin.commons.lib.kafka.config;


import com.leftbin.commons.lib.kafka.handler.GlobalConsumerErrorHandler;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaDefaultConsumerFactory {

    private final KafkaCommonConfig kafkaCommonConfig;


    public KafkaDefaultConsumerFactory(KafkaCommonConfig kafkaCommonConfig) {
        this.kafkaCommonConfig = kafkaCommonConfig;
    }

    private <K, V> ConsumerFactory<K, V> defaultConsumerFactory(K key, V value) {
        Map<String, Object> props = new HashMap<>(kafkaCommonConfig.getConsumerProducerConfig());
        props.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_KEY_TYPE, key.getClass().getName());
        props.put(KafkaProtobufDeserializerConfig.SPECIFIC_PROTOBUF_VALUE_TYPE, value.getClass().getName());
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.leftbin.commons.lib.kafka.interceptors.ConsumerInterceptor");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    public <K, V> ConcurrentKafkaListenerContainerFactory<K, V> defaultContainerFactory(K key, V value) {
        ConcurrentKafkaListenerContainerFactory<K, V> containerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        var cosumerFactory = defaultConsumerFactory(key, value);
        containerFactory.setConsumerFactory(cosumerFactory);
        containerFactory.setCommonErrorHandler(new GlobalConsumerErrorHandler());
        return containerFactory;
    }
}
