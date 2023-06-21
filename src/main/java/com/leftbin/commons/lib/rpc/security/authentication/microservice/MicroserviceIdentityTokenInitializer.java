package com.leftbin.commons.lib.rpc.security.authentication.microservice;

import com.auth0.exception.Auth0Exception;
import com.leftbin.commons.lib.rpc.security.authentication.config.AuthenticationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Component that initializes the microservice identity token upon application startup.
 * It listens for the ApplicationReadyEvent and then fetches and sets the microservice identity token.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MicroserviceIdentityTokenInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final MicroserviceIdentityHolder microserviceIdentityHolder;
    private final MicroserviceIdentityTokenFetcher microserviceIdentityTokenFetcher;
    private final AuthenticationConfig authenticationConfig;

    /**
     * This method is triggered when the ApplicationReadyEvent is published i.e., when the application is ready to serve requests.
     * It fetches the microservice identity token and sets it in the MicroserviceIdentityHolder.
     *
     * @param event the ApplicationReadyEvent published when the application is ready.
     */
    @Override
    public void onApplicationEvent(@NotNull final ApplicationReadyEvent event) {
        try {
            if (!authenticationConfig.getMicroserviceIdentity().isEnabled()) {
                log.info("skipping microservice machine-account identity initialization as it is disabled");
                return;
            }
            log.info("initializing microservice machine account identity");
            microserviceIdentityHolder.setTokenHolder(microserviceIdentityTokenFetcher.fetch());
            log.info("successfully initialized microservice with account id {} and {} email",
                    microserviceIdentityHolder.getMachineAccountId(),
                    microserviceIdentityHolder.getMachineAccountEmail());
        } catch (Auth0Exception e) {
            e.printStackTrace();
            log.error("failed to initialize microservice identity with error: {}", e.getMessage());
        }
    }
}
