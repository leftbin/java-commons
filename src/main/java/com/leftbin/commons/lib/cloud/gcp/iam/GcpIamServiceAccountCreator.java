package com.leftbin.commons.lib.cloud.gcp.iam;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.iam.v1.model.CreateServiceAccountRequest;
import com.google.api.services.iam.v1.model.ServiceAccount;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Utility class for creating service accounts in Google Cloud Platform.
 * <p>
 * This class provides a method to create a service account in Google Cloud Platform using
 * GoogleCredentials, the project's ID, and the desired service account name.
 *
 * <a href="https://cloud.google.com/iam/docs/samples/iam-create-service-account#iam_create_service_account-java"/>
 */
public class GcpIamServiceAccountCreator {

    /**
     * Creates a service account in the specified Google Cloud project.
     *
     * @param credential         The GoogleCredentials used to authenticate with Google Cloud IAM.
     * @param projectId          The ID of the Google Cloud project where the service account will
     *                           be created.
     * @param serviceAccountName The desired name for the new service account.
     * @return ServiceAccount    The created service account.
     * @throws GeneralSecurityException If there is a security exception during the service
     *                                  initialization.
     * @throws IOException              If there is an IO exception during the service account
     *                                  creation.
     */
    public static ServiceAccount create(GoogleCredentials credential, String projectId, String serviceAccountName)
        throws GeneralSecurityException, IOException {
        var service = GcpIamServiceBuilder.build(credential, "service-accounts");
        ServiceAccount serviceAccount = new ServiceAccount();
        serviceAccount.setDisplayName(serviceAccountName);
        var request = new CreateServiceAccountRequest();
        request.setAccountId(serviceAccountName);
        request.setServiceAccount(serviceAccount);
        try {
            return service.projects().serviceAccounts().create("projects/" + projectId, request).execute();
        } catch (GoogleJsonResponseException e) {
            // Check if the exception is because the service account already exists
            if (e.getStatusCode() == 409) { // 409 is the HTTP status code for Conflict
                return null; // or return the existing service account if needed
            } else {
                // If it's a different exception, propagate it
                throw e;
            }
        }
    }
}
