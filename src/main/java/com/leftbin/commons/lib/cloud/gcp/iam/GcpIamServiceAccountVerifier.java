package com.leftbin.commons.lib.cloud.gcp.iam;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.iam.v1.model.ServiceAccount;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GcpIamServiceAccountVerifier {
    // Verifies if a service account exists in the specified Google Cloud project.
    public static Boolean verify(GoogleCredentials credential, String projectId, String serviceAccountName)
        throws GeneralSecurityException, IOException {
        var iamService = GcpIamServiceBuilder.build(credential, "service-accounts");

        try {
            // List service accounts
            String resourceName = "projects/" + projectId;
            var serviceAccounts = iamService.projects().serviceAccounts().list(resourceName).execute().getAccounts();

            // Check if the desired service account is in the list
            for (ServiceAccount existingServiceAccount : serviceAccounts) {
                if (existingServiceAccount.getEmail().equals(
                    GcpIamServiceAccountEmailBuilder.build(projectId, serviceAccountName))) {
                    return true;
                }
            }
            return false;
        } catch (GoogleJsonResponseException e) {
            // If an exception is thrown, check if it is a 404 not found exception
            if (e.getStatusCode() == 404) {
                // If it's a 404 exception, return false as the service account does not exist
                return false;
            } else {
                // If it's a different exception, propagate it
                throw e;
            }
        }
    }
}
