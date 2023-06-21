package com.leftbin.commons.lib.database.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisStreamService {
    public static final String REDIS_STREAM_OFFSET_STARTING_MARKER = "0-0";
    private final RedisTemplate<String, String> redisTemplate;

    public void produce(String streamKey, String fieldName, String fieldValue) {
        var fields = new HashMap<String, String>();
        fields.put(fieldName, fieldValue);
        StringRecord record = StreamRecords.string(fields).withStreamKey(streamKey);
        redisTemplate.opsForStream().add(record);
    }

    public void createConsumerGroup(String streamKey, String groupName) {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            // Use the RedisStreamCommands to execute the XGROUP CREATE command
            RedisStreamCommands commands = connection.streamCommands();
            commands.xGroupCreate(
                streamKey.getBytes(),
                groupName,
                ReadOffset.from(REDIS_STREAM_OFFSET_STARTING_MARKER),
                true
            );
            return null;
        });
    }
}
