package com.leftbin.commons.lib.kafka.util;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.springframework.messaging.support.MessageBuilder;

public class DebugData {

    private DebugData() {
        throw new IllegalStateException("debug data class");
    }

    public static String getDebugStringOfkafkaData (Object data) {
        return data instanceof MessageBuilder ? TextFormat.shortDebugString((MessageOrBuilder) data) : data.toString();
    }

}
