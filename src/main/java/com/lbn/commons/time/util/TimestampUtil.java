package com.lbn.commons.time.util;

import com.google.protobuf.util.Timestamps;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * timestamp(ts) util
 */
public class TimestampUtil {
    private static final String UTC = "UTC";

    private TimestampUtil() {
        throw new IllegalStateException("TsUtil class");
    }

    public static Timestamp getCurrentTs() {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of(UTC));
        return Timestamp.valueOf(utc.toLocalDateTime());
    }

    public static com.google.protobuf.Timestamp convert(Timestamp sqlTimstamp) {
        if (sqlTimstamp == null) {
            return null;
        }
        return Timestamps.fromMillis(sqlTimstamp.getTime());
    }

    public static com.google.protobuf.Timestamp getCurrentProtobufTimestamp() {
        return Timestamps.fromMillis(getCurrentTs().getTime());
    }

    public static Timestamp convert(com.google.protobuf.Timestamp protobufTimestamp) {
        if (protobufTimestamp == null) {
            return null;
        }
        return Timestamp.from(Instant.ofEpochSecond(protobufTimestamp.getSeconds(),
                protobufTimestamp.getNanos()));
    }
}
