package com.lbn.commons.security;

import com.auth0.exception.Auth0Exception;
import com.lbn.commons.env.exception.EnvVarMissingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TokenRotatorCronJob {
    //start cron after 2 minutes and then run it every 10 minutes
    @Scheduled(initialDelay = 2, fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void scheduleFixedDelayTask() throws EnvVarMissingException, Auth0Exception {
        log.debug("checking to rotate microservice identity token");
        try {
            AuthUtil.rotateToken();
        } catch (EnvVarMissingException | Auth0Exception e) {
            log.error("failed to rotate microservice identity token with error {}", e.getMessage());
            throw e;
        }
    }
}
