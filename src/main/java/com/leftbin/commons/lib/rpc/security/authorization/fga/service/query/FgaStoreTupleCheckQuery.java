package com.leftbin.commons.lib.rpc.security.authorization.fga.service.query;

import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import com.leftbin.commons.lib.rpc.security.authorization.fga.dto.FgaCheckQueryRespDto;
import com.leftbin.commons.lib.rpc.security.authorization.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.lib.rpc.security.authorization.fga.middleware.Auth0FgaMiddleware;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaTupleKeyCheckModel;
import com.leftbin.commons.lib.rpc.security.authorization.fga.token.Auth0FgaApiTokenHolder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class FgaStoreTupleCheckQuery {
    private final Auth0FgaApiTokenHolder auth0FgaApiTokenHolder;
    private final AuthorizationConfig authorizationConfig;
    private final Auth0FgaMiddleware auth0FgaMiddleware;

    public boolean run(OpenFgaTupleKeyCheckModel input) throws Auth0FgaApiException {
        try {
            var url = getStoreTupleCheckUrl();
            var reqBody = new ObjectMapper().writeValueAsString(input);

            log.debug("processing request to post fga check api call with request body: {}", reqBody);

            try (var resp = auth0FgaMiddleware.post(url, auth0FgaApiTokenHolder.getAccessToken(), reqBody)) {
                if (resp.body() == null) {
                    throw new Auth0FgaApiException("response body from fga cannot be null");
                }

                var jsonResp = resp.body().string();
                log.debug("received good response for request to fga check api call with request body: {}", reqBody);

                var objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                var respDto = objectMapper.readValue(jsonResp, FgaCheckQueryRespDto.class);
                return respDto.isAllowed();
            }
        } catch (IOException e) {
            log.error("request to fga check api call failed with error {}", e.getMessage());
            throw new Auth0FgaApiException("an error occurred while processing the fga check api request", e);
        }
    }

    private String getStoreTupleCheckUrl() {
        return String.format("%s/stores/%s/check", authorizationConfig.getAuth0Fga().getApiEndpoint(), authorizationConfig.getAuth0Fga().getStoreId());
    }
}
