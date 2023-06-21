package com.leftbin.commons.lib.kafka.config;

import com.leftbin.commons.lib.kafka.strategy.TopicKeyStrategy;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class KafkaCommonConfigBase {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value(value = "${spring.kafka.properties.sasl.jaas.config}")
    private String saslJassConfig;

    @Value(value = "${spring.kafka.properties.sasl.mechanism}")
    private String saslMechanism;

    @Value(value = "${spring.kafka.properties.security.protocol}")
    private String securityProtocol;

    @Value(value = "${kafka.schema-registry.url}")
    private String schemaRegistryUrl;

    @Value(value = "${kafka.schema-registry.user-info}")
    private String schemaRegistryUserInfo;

    @Value(value = "${kafka.schema-registry.auth-credentials-source}")
    private String authCredentialsSource;


    public Map<String, String> getSchemaRegistryConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, authCredentialsSource);
        config.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, schemaRegistryUserInfo);
        return config;
    }
    private final String PROP_KEY_SASL_ENDPOINT_IDENTIFICATION_ALGORITHM = "ssl.endpoint.identification.algorithm";

    public Map<String, Object> getSaslConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJassConfig);
        props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        return props;
    }

    public Map<String, Object> getConsumerProducerConfig() {
        Map<String, Object> props = new HashMap<>(getSaslConfig());
        props.putAll(getSchemaRegistryConfig());
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer");
        props.put(PROP_KEY_SASL_ENDPOINT_IDENTIFICATION_ALGORITHM, "");
        props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 5242880);
        return props;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }
}
