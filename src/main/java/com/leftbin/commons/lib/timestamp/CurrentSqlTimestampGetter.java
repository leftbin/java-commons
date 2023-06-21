package com.leftbin.commons.lib.timestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Utility class for getting the current timestamp in SQL format.
 *
 * This class provides a method for retrieving the current timestamp, converted
 * to Coordinated Universal Time (UTC) and formatted as a java.sql.Timestamp.
 *
 */
public class CurrentSqlTimestampGetter {
    private static final String UTC = "UTC";

    /**
     * Returns the current timestamp in UTC as a SQL Timestamp.
     *
     * The method retrieves the current LocalDateTime, converts it to ZonedDateTime
     * using the system's default timezone, then converts the time to UTC. The
     * UTC ZonedDateTime is then converted to a LocalDateTime and finally to a SQL Timestamp.
     *
     * @return The current timestamp, in UTC, as a SQL Timestamp.
     */
    public static Timestamp get() {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of(UTC));
        return Timestamp.valueOf(utc.toLocalDateTime());
    }
}
