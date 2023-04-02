package com.leftbin.commons.security.service;

import com.leftbin.commons.context.RequestContext;
import com.leftbin.commons.rpc.query.IQuery;
import com.leftbin.commons.security.fga.exception.Auth0FgaApiException;
import com.leftbin.commons.security.fga.model.OpenFgaTupleKeyCheckModel;
import com.leftbin.commons.security.fga.model.OpenFgaTupleKeyWriteModel;
import com.leftbin.commons.security.fga.service.query.FgaStoreTupleCheckQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@Lazy
public class QueryAuthorizationService {
    private final FgaStoreTupleCheckQuery fgaStoreTupleCheckQuery;

    public IQuery.QueryAuthorizationResult authorize(String resourceId) {
        var authResponseBuilder = IQuery.QueryAuthorizationResult
                .builder()
                .isAuthorizationVerified(Boolean.TRUE)
                .isAuthorized(Boolean.TRUE);

        if (!StringUtils.hasText(RequestContext.getEmail())) {
            authResponseBuilder
                    .isAuthorizationVerified(Boolean.FALSE)
                    .unauthorizedReason("invalid user")
                    .error(IQuery.QueryError.builder()
                            .userDescription("invalid user")
                            .technicalDescription("invalid user")
                            .build()
                    );
            return authResponseBuilder.build();
        }

        var authorizationConfig = RequestContext.getAuthorizationConfig();

        if (!StringUtils.hasText(authorizationConfig.getPermission())) {
            return authResponseBuilder.build();
        }

        var fgaTupleKeyWriteModel = OpenFgaTupleKeyWriteModel.builder()
                .user(String.format("user:%s", RequestContext.getEmail()))
                .relation(authorizationConfig.getPermission())
                .object(String.format("%s:%s", authorizationConfig.getResourceType(), resourceId))
                .build();

        try {
            boolean isAuthorized = fgaStoreTupleCheckQuery.run(OpenFgaTupleKeyCheckModel
                    .builder()
                    .tupleKey(fgaTupleKeyWriteModel)
                    .build());

            if (!isAuthorized) {
                log.warn("authorization denied as {} principal does not have {} permission on {} object",
                        fgaTupleKeyWriteModel.getUser(),
                        fgaTupleKeyWriteModel.getRelation(),
                        fgaTupleKeyWriteModel.getObject()
                );
                authResponseBuilder
                        .isAuthorized(Boolean.FALSE)
                        .unauthorizedReason(authorizationConfig.getErrorMsg())
                        .error(IQuery.QueryError.builder()
                                .userDescription(authorizationConfig.getErrorMsg())
                                .technicalDescription(authorizationConfig.getErrorMsg())
                                .build()
                        );

            }
        } catch (IOException | Auth0FgaApiException ex) {
            ex.printStackTrace();
            log.error("Failed to authorize request, error: {}", ex.getMessage(), ex);
            return authResponseBuilder
                    .isAuthorizationVerified(Boolean.FALSE)
                    .error(IQuery.QueryError.builder()
                            .userDescription("unable to authorize request due to internal error")
                            .technicalDescription(String.format("unable to check authorization with fga due to %s", ex.getMessage()))
                            .build())
                    .build();
        }

        log.debug("authorization successful for tuple: {}", fgaTupleKeyWriteModel);
        return authResponseBuilder.build();
    }


}
