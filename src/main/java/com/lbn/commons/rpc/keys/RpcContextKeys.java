package com.lbn.commons.rpc.keys;

import io.grpc.Context;

public final class RpcContextKeys {
    private RpcContextKeys() {
        throw new RuntimeException("Not Allowed");
    }
    public static final Context.Key<String> USER_ACCOUNT_ID = Context.key("USER_ACCOUNT_ID");
    public static final Context.Key<String> REQUEST_ID = Context.key("X_REQUEST_ID");
    public static final Context.Key<String> EMAIL = Context.key("EMAIL");
    public static final Context.Key<String> MICROSERVICE_MACHINE_ACCOUNT_ID = Context.key("MICROSERVICE_MACHINE_ACCOUNT_ID");
    public static final Context.Key<String> ACCESS_TOKEN = Context.key("ACCESS_TOKEN");
}
