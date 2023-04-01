package com.leftbin.commons.kubernetes;

import com.google.auth.oauth2.GoogleCredentials;
import com.leftbin.commons.encoding.base64.Base64StringFileUploadPrefixRemover;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.Config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class KubernetesUtil {

    private KubernetesUtil() {
        throw new IllegalStateException("Utility class");
    }

    // this method returns the base64 encoded kubeconfig generated from the template that uses cluster endpoint,
    // ca data and oauth token generated from the google service account credential key.
    public static String getKubeconfigWithExec(String gsaKeyBase64, String clusterEndpoint, String clusterCaData) {
        //the format string requires the following three arguments in the same order
        //1. cluster endpoint
        //2. cluster ca-data
        //3. base64 encoded service account key
        var kubeconfigFormatString = """
            apiVersion: v1
            kind: Config
            current-context: kube-context
            contexts: [{name: kube-context, context: {cluster: kube-cluster, user: kube-user}}]
            clusters:
            - name: kube-cluster
              cluster:
                server: https://%s
                certificate-authority-data: %s
            users:
            - name: kube-user
              user:
                exec:
                  apiVersion: client.authentication.k8s.io/v1
                  interactiveMode: Never
                  command: /usr/local/bin/kube-client-go-google-credential-plugin
                  args:
                    - %s
            """;
        gsaKeyBase64 = Base64StringFileUploadPrefixRemover.remove(gsaKeyBase64);
        var kubeconfig = String.format(kubeconfigFormatString, clusterEndpoint, clusterCaData, gsaKeyBase64);
        return Base64.getEncoder().encodeToString(kubeconfig.getBytes());
    }

    // this method returns the base64 encoded kubeconfig generated from the template that uses cluster endpoint,
    // ca data and oauth token generated from the google service account credential key.
    public static String getKubeconfigWithToken(String gsaKeyBase64, String clusterEndpoint, String clusterCaData) throws IOException {
        gsaKeyBase64 = Base64StringFileUploadPrefixRemover.remove(gsaKeyBase64);
        var gsaKey = Base64.getDecoder().decode(gsaKeyBase64.getBytes());
        var cloudPlatformScope = "https://www.googleapis.com/auth/cloud-platform";
        var credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(gsaKey)).createScoped(cloudPlatformScope);
        credentials.refresh();
        var token = credentials.getAccessToken();
        //the format string requires the following three arguments in the same order
        //1. cluster endpoint
        //2. cluster ca-data
        //3. token
        var kubeconfigFormatString = """
            apiVersion: v1
            kind: Config
            current-context: kube-context
            contexts: [{name: kube-context, context: {cluster: kube-cluster, user: kube-user}}]
            clusters:
            - name: kube-cluster
              cluster:
                server: https://%s
                certificate-authority-data: %s
            users:
            - name: kube-user
              user:
                token: %s
            """;
        var kubeconfig = String.format(kubeconfigFormatString, clusterEndpoint, clusterCaData, token.getTokenValue());
        return Base64.getEncoder().encodeToString(kubeconfig.getBytes());
    }

    //returns a kubernetes client created from
    public static ApiClient getClient(String gsaKeyBase64, String clusterEndpoint, String clusterCaData) throws IOException {
        gsaKeyBase64 = Base64StringFileUploadPrefixRemover.remove(gsaKeyBase64);
        var kubeconfigBase64Encoded = getKubeconfigWithToken(gsaKeyBase64, clusterEndpoint, clusterCaData);
        var kubeconfigBase64Decoded = Base64.getDecoder().decode(kubeconfigBase64Encoded);
        return Config.fromConfig(new ByteArrayInputStream(kubeconfigBase64Decoded));
    }
}
