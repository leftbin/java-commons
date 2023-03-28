package com.lbn.commons.security.fga.config;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Lazy
@Configuration
@ConfigurationProperties(prefix = "auth0.fga")
@EqualsAndHashCode(callSuper = false)
@Data
public class Auth0FgaConfig {
    private String apiEndpoint;
    private String apiAudience;
    private String storeId;
    private String storeClientId;
    private String storeClientSecret;
}
