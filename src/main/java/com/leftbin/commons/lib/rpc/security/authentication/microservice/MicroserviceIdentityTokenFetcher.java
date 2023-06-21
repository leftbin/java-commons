package com.leftbin.commons.lib.rpc.security.authentication.microservice;

import com.leftbin.commons.lib.rpc.security.authentication.config.AuthenticationConfig;
import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component to fetch the Access Token for microservice identity from the Auth0 server.
 * It uses AuthAPI to perform the authentication process and return the token.
 */
@Component
@RequiredArgsConstructor
public class MicroserviceIdentityTokenFetcher {
    private final AuthenticationConfig authenticationConfig;

    /**
     * Fetches the Access Token from the Auth0 server using the configuration properties of the microservice.
     *
     * @return A TokenHolder containing the Access Token.
     * @throws Auth0Exception if the request to the Auth0 server fails.
     */
    public TokenHolder fetch() throws Auth0Exception {
        var userAuthApi = AuthAPI.newBuilder(authenticationConfig.getIdpDomain(),
                authenticationConfig.getMicroserviceIdentity().getClientId()
            ).withClientSecret(authenticationConfig.getMicroserviceIdentity().getClientSecret())
            .build();
        var tokenHolder = userAuthApi.requestToken(authenticationConfig.getApiAudience()).execute();
        return tokenHolder.getBody();
    }
}
