package com.leftbin.commons.lib.mapper;

import com.leftbin.commons.lib.timestamp.TimestampConverter;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring")
public abstract class ConversionMappers {
    public String[][] map(Map<String, String> stringMap) {
        return stringMap.entrySet().stream()
            .map(entry -> List.of(entry.getKey(), entry.getValue()))
            .map(list -> list.toArray(String[]::new))
            .toArray(String[][]::new);
    }

    public Map<String, String> map(String[][] mapArray) {
        return (Map)(mapArray == null ? new HashMap() : (Map) Arrays.stream(mapArray).collect(Collectors.toMap((pair) -> {
            return pair[0];
        }, (pair) -> {
            return pair[1];
        })));
    }

    public Timestamp map(com.google.protobuf.Timestamp protobufTimestamp) {
        return TimestampConverter.convert(protobufTimestamp);
    }

    public com.google.protobuf.Timestamp map(java.sql.Timestamp sqlTimeStamp) {
        return TimestampConverter.convert(sqlTimeStamp);
    }
}
