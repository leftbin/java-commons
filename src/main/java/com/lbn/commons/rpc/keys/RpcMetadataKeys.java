package com.lbn.commons.rpc.keys;

import io.grpc.Metadata;

public final class RpcMetadataKeys {
    private RpcMetadataKeys() {
        throw new RuntimeException("Not Allowed");
    }

    public static final Metadata.Key<String> X_REQUEST_ID =
        Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);
}
