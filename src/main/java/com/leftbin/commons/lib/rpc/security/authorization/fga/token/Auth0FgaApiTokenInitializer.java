package com.leftbin.commons.lib.rpc.security.authorization.fga.token;

import com.auth0.exception.Auth0Exception;
import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * This class listens to the ApplicationReadyEvent and initializes the Auth0 Fine-grained Access Control (FGA) API
 * authentication token when the application is fully started. It fetches the token using Auth0FgaApiTokenFetcher
 * and sets the token in Auth0FgaApiTokenHolder.
 * <p>
 * If an error occurs during the token fetch, it logs the error message.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Auth0FgaApiTokenInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final Auth0FgaApiTokenHolder auth0FgaApiTokenHolder;
    private final Auth0FgaApiTokenFetcher auth0FgaApiTokenFetcher;
    private final AuthorizationConfig authorizationConfig;

    /**
     * Handles the ApplicationReadyEvent. It initializes the Auth0 FGA API token when the application is ready.
     *
     * @param event the ApplicationReadyEvent object that the application publishes when it is fully started.
     */
    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        try {
            if(!authorizationConfig.isEnabled()) {
                return;
            }
            log.info("initializing auth0-fga api authentication token");
            auth0FgaApiTokenHolder.setTokenHolder( auth0FgaApiTokenFetcher.fetch());
            log.info("successfully initialized auth0-fga api authentication token");
        } catch (Auth0Exception e) {
            e.printStackTrace();
            log.error("failed to initialize auth0-fga authentication token with error: {}", e.getMessage());
        }
    }
}
