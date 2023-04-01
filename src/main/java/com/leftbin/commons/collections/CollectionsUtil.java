package com.leftbin.commons.collections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class CollectionsUtil {
    private CollectionsUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static Map<String, String> convert(String[][] mapArray) {
        if(mapArray == null) {
            return new HashMap<>();
        }
        //https://stackoverflow.com/a/31693843
        return Arrays.stream(mapArray).collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));
    }
}
