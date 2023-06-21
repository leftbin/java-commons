package com.leftbin.commons.lib.cloud.gcp.iam;

import com.google.api.services.iam.v1.model.CreateServiceAccountKeyRequest;
import com.google.api.services.iam.v1.model.ServiceAccountKey;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.security.GeneralSecurityException;

//https://cloud.google.com/iam/docs/samples/iam-create-key
public class GcpIamServiceAccountKeyCreator {
    public static ServiceAccountKey create(GoogleCredentials credential, String serviceAccountEmail)
        throws GeneralSecurityException, IOException {
        var service = GcpIamServiceBuilder.build(credential, "service-account-keys");
        return service.projects().serviceAccounts().keys()
            .create("projects/-/serviceAccounts/" + serviceAccountEmail,
                new CreateServiceAccountKeyRequest())
            .execute();
    }
}
