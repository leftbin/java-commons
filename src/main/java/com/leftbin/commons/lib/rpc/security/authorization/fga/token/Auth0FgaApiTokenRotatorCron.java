package com.leftbin.commons.lib.rpc.security.authorization.fga.token;

import com.leftbin.commons.lib.rpc.security.authentication.token.AuthenticationTokenExpiryVerifier;
import com.auth0.exception.Auth0Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * This class is responsible for rotating the Auth0 Fine-grained Access Control (FGA) API token when it is nearing
 * expiration. This is achieved by running a cron job every 10 minutes to check if the token is about to expire,
 * and if it is, fetches a new token and replaces the current one.
 *
 * The token is considered to be about to expire if there are less than TOKEN_EXPIRY_MINUTES_LIMIT minutes
 * before the token expires.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Auth0FgaApiTokenRotatorCron {
    private final static long TOKEN_EXPIRY_MINUTES_LIMIT = 10;
    private final Auth0FgaApiTokenHolder auth0FgaApiTokenHolder;
    private final AuthenticationTokenExpiryVerifier authenticationTokenExpiryVerifier;
    private final Auth0FgaApiTokenFetcher auth0FgaApiTokenFetcher;

    /**
     * This method is a scheduled cron job that runs every 10 minutes after an initial delay of 2 minutes.
     * It checks if the Auth0 FGA API token is about to expire, and if it is, fetches a new token
     * and replaces the current one in the Auth0FgaApiTokenHolder.
     *
     * @throws Auth0Exception if an error occurs while fetching a new token
     */
    @Scheduled(initialDelay = 2, fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void checkAndRotateFgaStoreToken() throws Auth0Exception {
        log.debug("running cron to check and rotate fga token");
        var isExpiring = authenticationTokenExpiryVerifier.verify(auth0FgaApiTokenHolder.getAccessToken(),
            TOKEN_EXPIRY_MINUTES_LIMIT);
        if (!isExpiring) {
            return;
        }
        log.info("auth0 fga token is about to expire in less than {} minutes... " +
            "rotating to fetch new token.", TOKEN_EXPIRY_MINUTES_LIMIT);
        auth0FgaApiTokenHolder.setTokenHolder(auth0FgaApiTokenFetcher.fetch());
    }
}
