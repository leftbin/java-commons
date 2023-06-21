package com.leftbin.commons.lib.cloud.gcp.secretsmanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretVersionName;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.zip.CRC32C;

/**
 * Fetcher for secret values stored in Google Cloud Platform's Secret Manager.
 * <p>
 * This component provides a method to retrieve the value of a secret from the Secret Manager
 * using GoogleCredentials, the project's id and the id of the secret.
 * <p>
 * This class is annotated as a Spring component, meaning it's a candidate for Spring's
 * dependency injection.
 **/
public class GcpSecretsManagerSecretValueFetcher {
    /**
     * Fetches a secret value from Google Cloud Secret Manager.
     * <p>
     * This method retrieves the secret value corresponding to a given projectId and secretId,
     * using the provided GoogleCredentials. It initializes a SecretManagerServiceClient to send
     * requests to the GCP Secret Manager, fetches the "latest" version of the secret, and
     * checks the payload's checksum to ensure data integrity.
     *
     * @param credentials The GoogleCredentials used to access the GCP Secret Manager.
     * @param secretName  The name of the secret to fetch from the Secret Manager in the format 'projects/<project-id>/secrets/<secret-name>'
     * @return The value of the fetched secret, as a String.
     * @throws IOException If there's an error fetching the secret or if data corruption is detected.
     */
    public static String fetch(GoogleCredentials credentials, String secretName) throws IOException {
        var secretManagerServiceClient = GcpSecretsManagerServiceClientBuilder.build(credentials);

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the "close" method on the client to safely clean up any remaining background resources.
        var secretVersionName = SecretVersionName.parse(String.format("%s/versions/latest", secretName));

        // Access the secret version.
        var response = secretManagerServiceClient.accessSecretVersion(secretVersionName);
        secretManagerServiceClient.close();
        // Verify checksum. The used library is available in Java 9+.
        // If using Java 8, you may use the following:
        // https://github.com/google/guava/blob/e62d6a0456420d295089a9c319b7593a3eae4a83/guava/src/com/google/common/hash/Hashing.java#L395
        byte[] data = response.getPayload().getData().toByteArray();
        var checksum = new CRC32C();
        checksum.update(data, 0, data.length);
        if (response.getPayload().getDataCrc32C() != checksum.getValue()) {
            throw new InvalidObjectException("data corruption detected while getting secret value");
        }

        return response.getPayload().getData().toStringUtf8();
    }
}
