package com.leftbin.commons.lib.cloud.gcp.credentials;

import com.google.api.services.iam.v1.IamScopes;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

public class GcpServiceAccountCredentialsBuilder {
    public static GoogleCredentials build(String base64EncodedServiceAccountKey) throws IOException {
        var decodedGsaKeyBytes = Base64.getDecoder().decode(base64EncodedServiceAccountKey);
        return GoogleCredentials.fromStream(new ByteArrayInputStream(decodedGsaKeyBytes))
            .createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));
    }
}
