package com.leftbin.commons.lib.kafka.strategy;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.serializers.subject.strategy.SubjectNameStrategy;

import java.util.Map;

public class TopicKeyStrategy implements SubjectNameStrategy {

    @Override
    public void configure(Map<String, ?> config) {
    }

    @Override
    public String subjectName(String topic, boolean isKey, ParsedSchema schema) {
        return isKey ? schema.name() + "-key" : topic + "-value";
    }
}
