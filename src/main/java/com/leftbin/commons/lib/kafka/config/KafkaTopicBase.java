package com.leftbin.commons.lib.kafka.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!selfie")
public class KafkaTopicBase {

    @Value("${kafka.group-id-suffix}")
    private String groupIdSuffix;

    @Value("${kafka.group-id-authoritative}")
    private String groupIdAuthoritative;

    public String getGroupIdSuffix() {
        return groupIdSuffix;
    }

    public void setGroupIdSuffix(String groupIdSuffix) {
        this.groupIdSuffix = groupIdSuffix;
    }

    public String getGroupId(String topic) {
        return String.format("%s-%s", topic, this.getGroupIdSuffix());
    }

    public String getGroupIdAuthoritative() {
        return groupIdAuthoritative;
    }

    public void setGroupIdAuthoritative(String groupIdAuthoritative) {
        this.groupIdAuthoritative = groupIdAuthoritative;
    }

    public String getId(String topic) {
        return String.format("%s-%s", this.getGroupId(topic), this.groupIdAuthoritative);
    }
}
