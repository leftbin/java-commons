package com.lbn.commons.context.util;

public class MethodNameUtil {

    private MethodNameUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static String getCurrentMethod() {
        return getCurrentMethod(1);
    }
    public static String getCurrentMethod(int skip) {
        return Thread.currentThread().getStackTrace()[1 + 1 + skip].getMethodName();
    }
}
