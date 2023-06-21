package com.leftbin.commons.lib.cloud.gcp.secretsmanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.ProjectName;
import com.google.cloud.secretmanager.v1.Replication;
import com.google.cloud.secretmanager.v1.Secret;

import java.io.IOException;

/**
 * Creator for secrets in Google Cloud Platform's Secret Manager.
 * <p>
 * This component provides a method to create a secret in the Secret Manager
 * using GoogleCredentials, the project's id, the name of the secret.
 * <a href="https://cloud.google.com/secret-manager/docs/samples/secretmanager-create-secret#secretmanager_create_secret-java">...</a>
 **/
public class GcpSecretsManagerSecretCreator {

    /**
     * Creates a secret in Google Cloud Secret Manager.
     * <p>
     * This method creates a secret with the given name in the Secret Manager,
     * using the provided GoogleCredentials. It initializes a SecretManagerServiceClient to send
     * requests to the GCP Secret Manager.
     *
     * @param credentials The GoogleCredentials used to access the GCP Secret Manager.
     * @param projectId   The id of the Google Cloud project where the secret will be stored.
     * @param secretName  The name of the secret to create in the Secret Manager.
     * @return secret created on google cloud secrets-manager
     * @throws IOException If there's an error creating the secret.
     */
    public static Secret create(GoogleCredentials credentials, String projectId, String secretName) throws IOException {
        var secretManagerServiceClient = GcpSecretsManagerServiceClientBuilder.build(credentials);
        var secret = secretManagerServiceClient.createSecret(
            ProjectName.of(projectId),
            secretName,
            Secret.newBuilder()
                .setReplication(Replication.newBuilder()
                    .setAutomatic(Replication.Automatic.newBuilder().build())
                    .build())
                .build());
        secretManagerServiceClient.close();
        return secret;
    }
}
