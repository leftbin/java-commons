package com.leftbin.commons.env.util;

import com.leftbin.commons.env.exception.EnvVarMissingException;

public class EnvUtil {
    public static void ensureEnvVar(String envVar) throws EnvVarMissingException {
        if (System.getenv(envVar) == null) {
            throw new EnvVarMissingException(envVar);
        }
    }

    public static String get(String envVarName, String defaultVal) {
        if (System.getenv(envVarName) == null) {
            return defaultVal;
        } else {
            return System.getenv(envVarName);
        }
    }
}
