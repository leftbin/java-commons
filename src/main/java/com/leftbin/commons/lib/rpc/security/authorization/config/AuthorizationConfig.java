package com.leftbin.commons.lib.rpc.security.authorization.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@ConfigurationProperties(prefix = "security.authorization")
@EqualsAndHashCode(callSuper = false)
@Data
public class AuthorizationConfig {
    private boolean enabled;
    private Auth0Fga auth0Fga;

    @Data
    public static class Auth0Fga {
        private String apiAudience;
        private String apiEndpoint;
        private String fgaDomain;
        private String storeClientId;
        private String storeClientSecret;
        private String storeId;
    }
}
