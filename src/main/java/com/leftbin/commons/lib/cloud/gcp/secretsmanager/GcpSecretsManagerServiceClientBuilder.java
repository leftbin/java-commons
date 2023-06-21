package com.leftbin.commons.lib.cloud.gcp.secretsmanager;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;

import java.io.IOException;

class GcpSecretsManagerServiceClientBuilder {
    public static SecretManagerServiceClient build(GoogleCredentials credentials) throws IOException {
        var secretManagerServiceSettings = SecretManagerServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
        return SecretManagerServiceClient.create(secretManagerServiceSettings);
    }
}
