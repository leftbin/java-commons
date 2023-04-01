package com.leftbin.commons.context;

import com.leftbin.commons.proto.v1.authz.extensions.AuthorizationConfig;
import io.grpc.Context;

import static com.leftbin.commons.rpc.keys.RpcContextKeys.*;


public class RequestContext {
    public static String getAccountUserId() {
        return USER_ACCOUNT_ID.get(Context.current());
    }

    public static String getEmail() {
        return EMAIL.get(Context.current());
    }

    public static String getMicroserviceMachineAccountId() {
        return MICROSERVICE_MACHINE_ACCOUNT_ID.get(Context.current());
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN.get(Context.current());
    }

    public static String getRequestId() {
        return REQUEST_ID.get(Context.current());
    }

    public static AuthorizationConfig getAuthorizationConfig() {
        return AUTHORIZATION_CONFIG.get(Context.current());
    }
}
