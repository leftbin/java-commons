package com.leftbin.commons.lib.timestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for getting the current timestamp as a string.
 *
 * This class provides a method for retrieving the current timestamp and formatting
 * it as a string in the "yyyyMMddHHmmss" format.
 *
 */
public class CurrentTimestampStringGetter {

    /**
     * Returns the current timestamp as a string.
     *
     * The method retrieves the current LocalDateTime, then formats it into
     * a string using the pattern "yyyyMMddHHmmss".
     *
     * @return The current timestamp, formatted as a string in the "yyyyMMddHHmmss" format.
     */
    public static String get() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
