package com.lbn.commons.security.fga.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbn.commons.security.fga.auth.Auth0FgaStoreAuth;
import com.lbn.commons.security.fga.config.Auth0FgaConfig;
import com.lbn.commons.security.fga.dto.FgaListObjectIdsQueryRespDto;
import com.lbn.commons.security.fga.dto.FgaListObjectsQueryRespDto;
import com.lbn.commons.security.fga.exception.Auth0FgaApiException;
import com.lbn.commons.security.fga.middleware.Auth0FgaMiddleware;
import com.lbn.commons.security.fga.model.OpenFgaListObjectsReqInputModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Lazy
@Component
@Slf4j
public class FgaStoreListObjectIdsQuery {
    private final Auth0FgaStoreAuth auth0FgaStoreAuth;
    private final Auth0FgaConfig auth0FgaConfig;
    private final Auth0FgaMiddleware auth0FgaMiddleware;

    public FgaStoreListObjectIdsQuery(Auth0FgaStoreAuth auth0FgaStoreAuth,
                                      Auth0FgaConfig auth0FgaConfig,
                                      Auth0FgaMiddleware auth0FgaMiddleware) {
        this.auth0FgaStoreAuth = auth0FgaStoreAuth;
        this.auth0FgaConfig = auth0FgaConfig;
        this.auth0FgaMiddleware = auth0FgaMiddleware;
    }

    public FgaListObjectIdsQueryRespDto run(OpenFgaListObjectsReqInputModel input) throws IOException, Auth0FgaApiException {
        var url = getStoreListObjectsUrl();
        var builder = FgaListObjectIdsQueryRespDto.builder();
        var reqBody = new ObjectMapper().writeValueAsString(input);

        log.debug("processing request to post fga list objects call with request body: {}", reqBody);
        var resp = auth0FgaMiddleware.post(url, auth0FgaStoreAuth.getAccessToken(), reqBody);
        var jsonResp = resp.body().string();
        log.debug("received good response for request to fga list objects call with request body: {}", reqBody);
        var objectsIds = new ObjectMapper().readValue(jsonResp, FgaListObjectsQueryRespDto.class)
            .getObjects().stream().map(object -> object.replace(String.format("%s:", input.getType()), "")).toList();
        return builder.objectIds(objectsIds).build();
    }

    private String getStoreListObjectsUrl() {
        return String.format("%s/stores/%s/list-objects", auth0FgaConfig.getApiEndpoint(), auth0FgaConfig.getStoreId());
    }
}
