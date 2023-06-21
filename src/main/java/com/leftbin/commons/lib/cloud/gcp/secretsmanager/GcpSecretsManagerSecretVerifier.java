package com.leftbin.commons.lib.cloud.gcp.secretsmanager;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;

/**
 * Checks if a secret exists on Google Cloud Secrets Manager
 **/
public class GcpSecretsManagerSecretVerifier {
    public static Boolean verify(GoogleCredentials credentials, String secretFullName) throws IOException {
        try (var secretManagerServiceClient = GcpSecretsManagerServiceClientBuilder.build(credentials)) {
            secretManagerServiceClient.getSecret(secretFullName);
            return true;
        } catch (ApiException e) {
            // Check if the status code is "NOT_FOUND"
            if (e.getStatusCode().getCode() == StatusCode.Code.NOT_FOUND) {
                return false;
            } else {
                throw e;
            }
        }
    }
}
