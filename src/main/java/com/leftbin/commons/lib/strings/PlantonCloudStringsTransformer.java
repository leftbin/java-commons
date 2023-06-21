package com.leftbin.commons.lib.strings;

public class PlantonCloudStringsTransformer {
    public static String hyphensWithUnderscores(String input) {
        return input.replaceAll("-", "_");
    }

    public static String underscoresWithHyphens(String input) {
        return input.replaceAll("_", "-");
    }
}
