package com.leftbin.commons.lib.kafka.config;

import com.leftbin.commons.lib.kafka.handler.DeserializationExceptionHandler;
import com.leftbin.commons.lib.kafka.handler.ProductionExceptionHandler;
import com.leftbin.commons.lib.kafka.handler.RestartStreamsOnUncaughtExceptionHandler;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@Lazy
public class KafkaCommonStreamsConfig extends KafkaCommonConfigBase {
    private static final Logger l = LoggerFactory.getLogger(KafkaCommonStreamsConfig.class);

    @Value(value = "${kafka.streams.min-in-sync-replicas}")
    private String minInSyncReplicas;

    @Value(value = "${kafka.streams.replication-factor}")
    private String replicationFactor;

    @Value(value = "${kafka.streams.host-name}")
    private String hostName;

    @Value(value = "${kafka.streams.port}")
    private String port;

    @Value(value = "${kafka.streams.application-id}")
    private String applicationId;

    @Value(value = "${kafka.streams.auto-offset-reset-config}")
    private String autoOffsetResetConfig;

    @Value(value = "${kafka.streams.state.dir}")
    private String stateDir;

    public Map<String, String> getChangelogConfig() {
        Map<String, String> changelogConfig = new HashMap<>();
        changelogConfig.put("min.insync.replicas", minInSyncReplicas);
        return changelogConfig;
    }

    public void closeKafkaStream(KafkaStreams streams) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                l.info("streams status before close: {}", streams.state());
                try {
                    streams.close();
                } catch (Exception e) {
                    l.error("exception while closing streams: {}", e.getMessage());
                }
                l.info("streams status after close: {}", streams.state());
            }
        });
    }

    public Map<String, Object> getStreamsConfig () {
        Map<String, Object> config = new HashMap<>(getSaslConfig());
        config.putAll(getSchemaRegistryConfig());
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        config.put(StreamsConfig.APPLICATION_SERVER_CONFIG, String.format("%s:%s", hostName, port));
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        config.put(StreamsConfig.CLIENT_ID_CONFIG, applicationId);
        config.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, replicationFactor);
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, DeserializationExceptionHandler.class);
        config.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, ProductionExceptionHandler.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig);
        config.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
        config.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60000);
        return config;
    }

    public KafkaStreams startKafkaStream(
            Topology topology
    ) {
        var streams = new KafkaStreams(topology, new KafkaStreamsConfiguration(getStreamsConfig()).asProperties());
        streams.setUncaughtExceptionHandler(new RestartStreamsOnUncaughtExceptionHandler());
        streams.start();
        closeKafkaStream(streams);
        return streams;
    }
}
