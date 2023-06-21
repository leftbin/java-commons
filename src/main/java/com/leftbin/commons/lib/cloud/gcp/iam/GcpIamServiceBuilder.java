package com.leftbin.commons.lib.cloud.gcp.iam;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;

class GcpIamServiceBuilder {
    public static Iam build(GoogleCredentials credential, String applicationName) throws GeneralSecurityException, IOException {
        // Initialize the IAM service, which can be used to send requests to the IAM API.
        return new Iam.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credential))
            .setApplicationName(applicationName)
            .build();
    }
}
