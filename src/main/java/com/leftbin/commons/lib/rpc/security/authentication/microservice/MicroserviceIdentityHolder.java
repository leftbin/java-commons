package com.leftbin.commons.lib.rpc.security.authentication.microservice;

import com.leftbin.commons.lib.rpc.security.authentication.config.AuthenticationConfig;
import com.auth0.json.auth.TokenHolder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Component responsible for holding the identity of a microservice. It includes the functionality to extract
 * information such as Machine Account ID and Email from the access token.
 **/
@Component
@Setter
@RequiredArgsConstructor
public class MicroserviceIdentityHolder {
    TokenHolder tokenHolder;
    private final AuthenticationConfig authenticationConfig;

    /**
     * Retrieves the Machine Account ID from the JWT access token subject field.
     *
     * @return the subject of the JWT token, which is the Machine Account ID.
     * @throws IllegalArgumentException if the token holder is null.
     */
    public String getMachineAccountId() {
        Assert.notNull(tokenHolder, "microservice identity token holder is null");
        var decodedJWT = JWT.decode(tokenHolder.getAccessToken());
        return decodedJWT.getSubject();
    }

    /**
     * Retrieves the Machine Account Email from the JWT access token using the custom claims key configured.
     *
     * @return the Machine Account Email if present, empty string otherwise.
     * @throws IllegalArgumentException if the token holder is null.
     */
    public String getMachineAccountEmail() {
        Assert.notNull(tokenHolder, "microservice identity token holder is null");
        var decodedJWT = JWT.decode(tokenHolder.getAccessToken());
        for (Map.Entry<String, Claim> entry : decodedJWT.getClaims().entrySet()) {
            if (entry.getKey().equals(authenticationConfig.getTokenCustomClaimsKey())) {
                return entry.getValue().asString();
            }
        }
        return "";
    }

    /**
     * Retrieves Machine Account Access Token from the Token Holder
     * @return microservice machine-account access token
     */
    public String getAccessToken() {
        return tokenHolder.getAccessToken();
    }
}
