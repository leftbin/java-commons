package com.leftbin.commons.lib.rpc.security.authorization.fga.service.query;

import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import com.leftbin.commons.lib.rpc.security.authorization.fga.dto.FgaListObjectIdsQueryRespDto;
import com.leftbin.commons.lib.rpc.security.authorization.fga.dto.FgaListObjectsQueryRespDto;
import com.leftbin.commons.lib.rpc.security.authorization.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.lib.rpc.security.authorization.fga.middleware.Auth0FgaMiddleware;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaListObjectsReqInputModel;
import com.leftbin.commons.lib.rpc.security.authorization.fga.token.Auth0FgaApiTokenHolder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class FgaStoreListObjectIdsQuery {
    private final Auth0FgaApiTokenHolder auth0FgaApiTokenHolder;
    private final AuthorizationConfig authorizationConfig;
    private final Auth0FgaMiddleware auth0FgaMiddleware;

    public FgaListObjectIdsQueryRespDto run(OpenFgaListObjectsReqInputModel input) throws Auth0FgaApiException {
        try {
            var url = getStoreListObjectsUrl();
            var builder = FgaListObjectIdsQueryRespDto.builder();
            var reqBody = new ObjectMapper().writeValueAsString(input);

            log.debug("processing request to post fga list objects call with request body: {}", reqBody);

            try (var resp = auth0FgaMiddleware.post(url, auth0FgaApiTokenHolder.getAccessToken(), reqBody)) {
                if (resp.body() == null) {
                    throw new Auth0FgaApiException("response body from fga cannot be null");
                }

                var jsonResp = resp.body().string();
                log.debug("received good response for request to fga list objects call with request body: {}", reqBody);

                var objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                var response = objectMapper.readValue(jsonResp, FgaListObjectsQueryRespDto.class);

                if (response != null) {
                    var objects = response.getObjects();
                    if (objects != null) {
                        var objectIds = objects.stream()
                            .map(object -> object.replace(String.format("%s:", input.getType()), ""))
                            .toList();
                        return builder.objectIds(objectIds).build();
                    } else {
                        return builder.objectIds(Collections.emptyList()).build();
                    }
                } else {
                    throw new Auth0FgaApiException("received null response from fga");
                }
            }
        } catch (IOException e) {
            log.error("request to list objects failed with error {}", e.getMessage());
            throw new Auth0FgaApiException("an error occurred while processing the request", e);
        }
    }

    private String getStoreListObjectsUrl() {
        return String.format("%s/stores/%s/list-objects", authorizationConfig.getAuth0Fga().getApiEndpoint(), authorizationConfig.getAuth0Fga().getStoreId());
    }
}
