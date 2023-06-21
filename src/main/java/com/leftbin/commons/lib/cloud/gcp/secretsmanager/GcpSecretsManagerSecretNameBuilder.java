package com.leftbin.commons.lib.cloud.gcp.secretsmanager;

public class GcpSecretsManagerSecretNameBuilder {
    public static String build(String projectNumber, String name) {
        return String.format("projects/%s/secrets/%s", projectNumber, name);
    }
}
