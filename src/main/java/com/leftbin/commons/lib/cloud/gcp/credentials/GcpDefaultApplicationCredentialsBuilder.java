package com.leftbin.commons.lib.cloud.gcp.credentials;

import com.google.api.services.iam.v1.IamScopes;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Collections;

public class GcpDefaultApplicationCredentialsBuilder {
    public static GoogleCredentials build() throws IOException {
        return GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));
    }
}
