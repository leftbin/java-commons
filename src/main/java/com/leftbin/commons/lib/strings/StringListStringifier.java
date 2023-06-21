package com.leftbin.commons.lib.strings;

import java.util.List;

/**
 * Utility class for converting a List of Strings into a single String.
 *
 * This class provides a method for converting a List of Strings into a single
 * String where each element in the list is separated by a comma.
 *
 */
public class StringListStringifier {
    /**
     * Converts a List of Strings into a single String, with elements separated by commas.
     *
     * This method uses the String.join function to concatenate all elements in the list into
     * a single String, separating each element by a comma.
     *
     * @param stringList The List of Strings to convert.
     * @return A single String containing all elements in the list, separated by commas.
     *         If the list is empty, returns an empty string.
     */
    public static String stringify(List<String> stringList) {
        return String.join(",", stringList);
    }
}
