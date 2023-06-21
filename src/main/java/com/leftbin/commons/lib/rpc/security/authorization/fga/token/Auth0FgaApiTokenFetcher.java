package com.leftbin.commons.lib.rpc.security.authorization.fga.token;

import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for fetching Auth0 Fine-grained Access Control (FGA) API tokens.
 * It utilizes the Auth0 client to authenticate with the Identity Provider (IdP) and retrieve the token.
 */
@Component
@RequiredArgsConstructor
public class Auth0FgaApiTokenFetcher {
    /**
     * Fetches the API token from the IdP. The token allows for authentication with
     * the FGA API.
     *
     * @return the TokenHolder instance, which contains the API token.
     * @throws Auth0Exception if there's an error during the token fetching process.
     */
    private final AuthorizationConfig authorizationConfig;

    public TokenHolder fetch() throws Auth0Exception {
        var userAuthApi = AuthAPI.newBuilder(authorizationConfig.getAuth0Fga().getFgaDomain(),
                authorizationConfig.getAuth0Fga().getStoreClientId())
            .withClientSecret(authorizationConfig.getAuth0Fga().getStoreClientSecret())
            .build();

        return userAuthApi.requestToken(authorizationConfig.getAuth0Fga().getApiAudience()).execute().getBody();
    }
}
