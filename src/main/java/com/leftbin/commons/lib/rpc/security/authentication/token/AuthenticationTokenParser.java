package com.leftbin.commons.lib.rpc.security.authentication.token;

import com.leftbin.commons.lib.rpc.security.authentication.config.AuthenticationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthenticationTokenParser {
    private final AuthenticationConfig authenticationConfig;

    public String parseEmail(Authentication authentication) {
        var tokenHolder = (JwtAuthenticationToken) authentication;
        for (Map.Entry<String, Object> entry : tokenHolder.getToken().getClaims().entrySet()) {
            if (entry.getKey().equals(authenticationConfig.getTokenCustomClaimsKey())) {
                return entry.getValue().toString();
            }
        }
        return "";
    }

    public String parseId(Authentication auth) {
        var tokenHolder = (JwtAuthenticationToken) auth;
        return tokenHolder.getToken().getSubject();
    }

    public String parseToken(Authentication auth) {
        var tokenHolder = (JwtAuthenticationToken) auth;
        return tokenHolder.getToken().getTokenValue();
    }
}
