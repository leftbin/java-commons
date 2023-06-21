package com.leftbin.commons.lib.timestamp;

import com.google.protobuf.util.Timestamps;

/**
 * Utility class for getting the current timestamp in protobuf format.
 *
 * This class provides a method for retrieving the current timestamp and formatting
 * it as a com.google.protobuf.Timestamp.
 *
 */
public class CurrentProtobufTimestampGetter {

    /**
     * Returns the current timestamp as a protobuf Timestamp.
     *
     * The method uses CurrentSqlTimestampGetter to get the current timestamp as a SQL Timestamp,
     * then converts the SQL Timestamp to a protobuf Timestamp by extracting the time in milliseconds.
     *
     * @return The current timestamp, formatted as a protobuf Timestamp.
     */
    public static com.google.protobuf.Timestamp get() {
        return Timestamps.fromMillis(CurrentSqlTimestampGetter.get().getTime());
    }
}
