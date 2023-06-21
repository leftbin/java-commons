package com.leftbin.commons.lib.cloud.gcp.iam;

public class GcpIamServiceAccountEmailBuilder {
    public static String build(String projectId, String name) {
        return String.format("%s@%s.iam.gserviceaccount.com", name, projectId);
    }
}
