package com.leftbin.commons.lib.rpc.security.authentication.microservice;

import com.leftbin.commons.lib.rpc.security.authentication.token.AuthenticationTokenExpiryVerifier;
import com.auth0.exception.Auth0Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Component that runs a scheduled task to rotate the microservice identity token.
 * This task checks if the token is expiring within a specified limit and fetches a new token if it is.
**/
@Component
@Slf4j
@RequiredArgsConstructor
public class MicroserviceIdentityTokenRotatorCron {
    private static final long TOKEN_EXPIRY_MINUTES_LIMIT = 10;
    private final AuthenticationTokenExpiryVerifier authenticationTokenExpiryVerifier;
    private final MicroserviceIdentityHolder microserviceIdentityHolder;
    private final MicroserviceIdentityTokenFetcher microserviceIdentityTokenFetcher;

    /**
     * The scheduled task that checks if the microservice identity token is about to expire and fetches a new token if it is.
     * This task is initially delayed for 2 minutes after the application start up and then it runs every 10 minutes.
     *
     * @throws Auth0Exception if there's an error during the token fetching process.
     */
    @Scheduled(initialDelay = 2, fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void scheduleFixedDelayTask() throws Auth0Exception {
        log.debug("checking to rotate microservice identity token");
        var isExpiring = authenticationTokenExpiryVerifier.verify(microserviceIdentityHolder.getAccessToken(), TOKEN_EXPIRY_MINUTES_LIMIT);
        if (!isExpiring) {
            return;
        }
        log.info("microservice machine account token is about to expire in less than {} minutes... " +
            "rotating to fetch new token.", TOKEN_EXPIRY_MINUTES_LIMIT);
        microserviceIdentityHolder.setTokenHolder(microserviceIdentityTokenFetcher.fetch());
        log.debug("rotated microservice identity token");
    }
}
