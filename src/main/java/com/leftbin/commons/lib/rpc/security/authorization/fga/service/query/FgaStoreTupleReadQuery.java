package com.leftbin.commons.lib.rpc.security.authorization.fga.service.query;

import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import com.leftbin.commons.lib.rpc.security.authorization.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.lib.rpc.security.authorization.fga.middleware.Auth0FgaMiddleware;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaTupleKeysReadInputModel;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaTupleKeysReadResponseModel;
import com.leftbin.commons.lib.rpc.security.authorization.fga.token.Auth0FgaApiTokenHolder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class FgaStoreTupleReadQuery {
    private final Auth0FgaApiTokenHolder auth0FgaApiTokenHolder;
    private final AuthorizationConfig authorizationConfig;
    private final Auth0FgaMiddleware auth0FgaMiddleware;

    public OpenFgaTupleKeysReadResponseModel run(OpenFgaTupleKeysReadInputModel input) throws Auth0FgaApiException {
        try {
            var url = getStoreTupleReadUrl();

            var reqBody = new ObjectMapper().writeValueAsString(input);

            log.debug("processing request to post fga read api call with request body: {}", reqBody);

            try (var resp = auth0FgaMiddleware.post(url, auth0FgaApiTokenHolder.getAccessToken(), reqBody)) {

                if (!resp.isSuccessful()) {
                    throw new Auth0FgaApiException("failed to get response from the api");
                }

                var jsonResp = Objects.requireNonNull(resp.body(), "fga response body is null").string();
                log.debug("processing response from fga check api call: {}", jsonResp);

                var objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                return objectMapper.readValue(jsonResp, OpenFgaTupleKeysReadResponseModel.class);
            }
        } catch (IOException e) {
            // Optionally convert to a runtime exception or handle accordingly
            throw new Auth0FgaApiException("An error occurred while processing the API request", e);
        }
    }

    private String getStoreTupleReadUrl() {
        return String.format("%s/stores/%s/read", authorizationConfig.getAuth0Fga().getApiEndpoint(), authorizationConfig.getAuth0Fga().getStoreId());
    }
}
