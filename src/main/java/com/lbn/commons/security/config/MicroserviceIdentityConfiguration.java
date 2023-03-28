package com.lbn.commons.security.config;

import com.lbn.commons.security.AuthUtil;
import com.lbn.commons.security.exception.MicroserviceIdentityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MicroserviceIdentityConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        try {
            log.info("initializing microservice machine account identity");
            AuthUtil.initializeMicroserviceIdentity();
            log.info("successfully initialized microservice with account id {} and {} email",
                AuthUtil.getMicroserviceMachineAccountId(),
                AuthUtil.getMicroserviceMachineAccountEmail());
        } catch (MicroserviceIdentityException e) {
            e.printStackTrace();
            log.error("failed to initialize microservice identity with error: {}", e.getMessage());
        }

    }
}
