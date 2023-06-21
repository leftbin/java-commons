package com.leftbin.commons.lib.cloud.gcp.secretsmanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretPayload;
import com.google.protobuf.ByteString;

import java.io.IOException;

public class GcpSecretsManagerSecretVersionSetter {
    //secretName should be in "projects/*/secrets/*" format
    public static void set(GoogleCredentials credentials, String secretName, String secretValue) throws IOException {
        var secretManagerServiceClient = GcpSecretsManagerServiceClientBuilder.build(credentials);
        secretManagerServiceClient.addSecretVersion(
            secretName,
            SecretPayload.newBuilder()
                .setData(ByteString.copyFromUtf8(secretValue))
                .build());
        secretManagerServiceClient.close();
    }
}
