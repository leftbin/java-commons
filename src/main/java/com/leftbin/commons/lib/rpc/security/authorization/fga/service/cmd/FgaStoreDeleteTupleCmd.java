package com.leftbin.commons.lib.rpc.security.authorization.fga.service.cmd;

import com.leftbin.commons.lib.rpc.security.authorization.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.lib.rpc.security.authorization.fga.middleware.Auth0FgaMiddleware;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import com.leftbin.commons.lib.rpc.security.authorization.fga.token.Auth0FgaApiTokenHolder;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaTupleKeyWriteModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Component
@Slf4j
public class FgaStoreDeleteTupleCmd {
    private final Auth0FgaApiTokenHolder auth0FgaApiTokenHolder;
    private final AuthorizationConfig authorizationConfig;
    private final Auth0FgaMiddleware auth0FgaMiddleware;

    public FgaStoreDeleteTupleCmd(Auth0FgaApiTokenHolder auth0FgaApiTokenHolder,
                                  AuthorizationConfig authorizationConfig,
                                  Auth0FgaMiddleware auth0FgaMiddleware) {
        this.auth0FgaApiTokenHolder = auth0FgaApiTokenHolder;
        this.authorizationConfig = authorizationConfig;
        this.auth0FgaMiddleware = auth0FgaMiddleware;
    }

    public void run(OpenFgaTupleKeyWriteModel input) throws IOException, Auth0FgaApiException {
        var url = getWriteTupleUrl();
        var reqBodyInput = new HashMap<String, Map<String, List<OpenFgaTupleKeyWriteModel>>>();
        var deletes = new HashMap<String, List<OpenFgaTupleKeyWriteModel>>();
        var tupleKeyList = new ArrayList<OpenFgaTupleKeyWriteModel>();
        tupleKeyList.add(input);
        deletes.put("tuple_keys", tupleKeyList);
        reqBodyInput.put("deletes", deletes);

        var reqBody = new ObjectMapper().writeValueAsString(reqBodyInput);

        log.debug("processing request to post write tuple api call with request body: {}", reqBody);
        var resp = auth0FgaMiddleware.post(url, auth0FgaApiTokenHolder.getAccessToken(), reqBody);
        var jsonResp = resp.body().string();
        log.debug("received response for request to write tuple api post request with request boyd: {}", jsonResp);
    }

    private String getWriteTupleUrl() {
        return String.format("%s/stores/%s/write", authorizationConfig.getAuth0Fga().getApiEndpoint(), authorizationConfig.getAuth0Fga().getStoreId());
    }
}
