package com.leftbin.commons.lib.rpc.security.authentication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to hold the properties for security authentication.
 * It uses Spring's @ConfigurationProperties to bind the environment properties prefixed with 'security.authentication'.
 **/
@Configuration
@ConfigurationProperties(prefix = "security.authentication")
@Data
public class AuthenticationConfig {
    private String idpDomain;
    private String idpUrl;
    private String apiAudience;
    private String tokenCustomClaimsKey;
    private MicroserviceAuthentication microservice;

    /**
     * Configuration class to hold the properties for microservice authentication.
     * It is nested within AuthenticationConfig to match the structure of the properties.
     */
    @Data
    public static class MicroserviceAuthentication {
        private String clientId;
        private String clientSecret;
    }
}
