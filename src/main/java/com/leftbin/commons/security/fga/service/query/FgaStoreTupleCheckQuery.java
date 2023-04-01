package com.leftbin.commons.security.fga.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftbin.commons.security.fga.auth.Auth0FgaStoreAuth;
import com.leftbin.commons.security.fga.config.Auth0FgaConfig;
import com.leftbin.commons.security.fga.dto.FgaCheckQueryRespDto;
import com.leftbin.commons.security.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.security.fga.middleware.Auth0FgaMiddleware;
import com.leftbin.commons.security.fga.model.OpenFgaTupleKeyCheckModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Lazy
@Component
@Slf4j
public class FgaStoreTupleCheckQuery {
    private final Auth0FgaStoreAuth auth0FgaStoreAuth;
    private final Auth0FgaConfig auth0FgaConfig;
    private final Auth0FgaMiddleware auth0FgaMiddleware;

    public FgaStoreTupleCheckQuery(Auth0FgaStoreAuth auth0FgaStoreAuth,
                                   Auth0FgaConfig auth0FgaConfig,
                                   Auth0FgaMiddleware auth0FgaMiddleware) {
        this.auth0FgaStoreAuth = auth0FgaStoreAuth;
        this.auth0FgaConfig = auth0FgaConfig;
        this.auth0FgaMiddleware = auth0FgaMiddleware;
    }

    public boolean run(OpenFgaTupleKeyCheckModel input) throws IOException, Auth0FgaApiException {
        var url = getStoreTupleCheckUrl();

        var reqBody = new ObjectMapper().writeValueAsString(input);

        log.debug("processing request to post fga check api call with request body: {}", reqBody);
        var resp = auth0FgaMiddleware.post(url, auth0FgaStoreAuth.getAccessToken(), reqBody);
        var jsonResp = resp.body().string();
        log.debug("processing request to post fga check api call with request body: {}", reqBody);
        var respDto = new ObjectMapper().readValue(jsonResp, FgaCheckQueryRespDto.class);
        return respDto.isAllowed();
    }

    private String getStoreTupleCheckUrl() {
        return String.format("%s/stores/%s/check", auth0FgaConfig.getApiEndpoint(), auth0FgaConfig.getStoreId());
    }
}
