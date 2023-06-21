package com.leftbin.commons.lib.timestamp;

import com.google.protobuf.util.Timestamps;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Utility class for converting between SQL Timestamp and protobuf Timestamp.
 *
 * This class provides two static methods for converting between java.sql.Timestamp and
 * com.google.protobuf.Timestamp. These methods can be used when you need to convert
 * Timestamps from database entries into protobuf messages or vice versa.
 *
 */
public class TimestampConverter {
    /**
     * Converts a SQL Timestamp to a protobuf Timestamp.
     *
     * @param sqlTimestamp The SQL Timestamp to convert.
     * @return The converted protobuf Timestamp, or null if the input is null.
     */
    public static com.google.protobuf.Timestamp convert(Timestamp sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }
        return Timestamps.fromMillis(sqlTimestamp.getTime());
    }

    /**
     * Converts a protobuf Timestamp to a SQL Timestamp.
     *
     * @param protobufTimestamp The protobuf Timestamp to convert.
     * @return The converted SQL Timestamp, or null if the input is null.
     */
    public static Timestamp convert(com.google.protobuf.Timestamp protobufTimestamp) {
        if (protobufTimestamp == null) {
            return null;
        }
        return Timestamp.from(Instant.ofEpochSecond(protobufTimestamp.getSeconds(),
            protobufTimestamp.getNanos()));
    }
}
