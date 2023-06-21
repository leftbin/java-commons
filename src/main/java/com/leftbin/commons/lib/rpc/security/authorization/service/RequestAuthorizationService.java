package com.leftbin.commons.lib.rpc.security.authorization.service;

import com.leftbin.commons.lib.rpc.request.context.RequestAuthContext;
import com.leftbin.commons.lib.rpc.security.authorization.config.AuthorizationConfig;
import com.leftbin.commons.lib.rpc.security.authorization.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaTupleKeyCheckModel;
import com.leftbin.commons.lib.rpc.security.authorization.fga.model.OpenFgaTupleKeyWriteModel;
import com.leftbin.commons.lib.rpc.security.authorization.fga.service.query.FgaStoreTupleCheckQuery;
import com.leftbin.commons.lib.step.StepResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestAuthorizationService {
    private final AuthorizationConfig authorizationConfig;
    private final FgaStoreTupleCheckQuery fgaStoreTupleCheckQuery;

    public StepResult authorize(String resourceId) {
        var stepBuilder = StepResult.newBuilder();
        //todo: re-implement using strategy pattern in java
        //https://chat.openai.com/share/4657f85c-dbf1-42ef-a4fa-c575ecf060e3
        return stepBuilder.build();
    }
}
