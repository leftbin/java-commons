package com.leftbin.commons.lib.rpc.security.authorization.fga.token;

import com.auth0.json.auth.TokenHolder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Setter
public class Auth0FgaApiTokenHolder {
    private TokenHolder tokenHolder;

    public String getAccessToken() {
        return tokenHolder.getAccessToken();
    }
}
