package com.lbn.commons.security.fga.auth;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.lbn.commons.security.AuthUtil;
import com.lbn.commons.security.fga.config.Auth0FgaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Lazy
@Component
@Slf4j
public class Auth0FgaStoreAuth {

    private final static long TOKEN_EXPIRY_MINUTES_LIMIT = 10;
    private final Auth0FgaConfig auth0FgaConfig;
    private static TokenHolder fgaStoreClientTokenHolder = null;

    public Auth0FgaStoreAuth(Auth0FgaConfig auth0FgaConfig) throws Auth0Exception {
        this.auth0FgaConfig = auth0FgaConfig;
        fgaStoreClientTokenHolder = exchangeCredentials();
    }

    private TokenHolder exchangeCredentials() throws Auth0Exception {
        log.info("fetching token from auth0 fga");
        var userAuthApi = new AuthAPI("fga.us.auth0.com", auth0FgaConfig.getStoreClientId(), auth0FgaConfig.getStoreClientSecret());
        return userAuthApi.requestToken(auth0FgaConfig.getApiAudience()).execute();
    }

    //start cron after 2 minutes and then run it every 10 minutes
    @Scheduled(initialDelay = 2, fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void checkAndRotateFgaStoreToken() throws Auth0Exception {
        log.debug("running cron to check and rotate fga token");
        if (!AuthUtil.isTokenExpiring(fgaStoreClientTokenHolder.getAccessToken(), TOKEN_EXPIRY_MINUTES_LIMIT)) {
            return;
        }
        log.info("auth0 fga token is about to expire in less than {} minutes... " +
            "rotating to fetch new token.", TOKEN_EXPIRY_MINUTES_LIMIT);
        fgaStoreClientTokenHolder = exchangeCredentials();
    }

    public String getAccessToken() {
        return fgaStoreClientTokenHolder.getAccessToken();
    }
}
