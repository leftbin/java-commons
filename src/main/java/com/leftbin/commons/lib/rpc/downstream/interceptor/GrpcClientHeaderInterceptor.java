package com.leftbin.commons.lib.rpc.downstream.interceptor;

import com.leftbin.commons.lib.rpc.downstream.config.DownstreamConfig;
import com.leftbin.commons.lib.rpc.security.authentication.microservice.MicroserviceIdentityHolder;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

/**
 * AN interceptor to handle client identity header.
 */
@Slf4j
public class GrpcClientHeaderInterceptor implements ClientInterceptor {
    private final MicroserviceIdentityHolder microserviceIdentityHolder;
    private final DownstreamConfig.Downstream downstream;
    static final Metadata.Key<byte[]> AUTHORIZATION_HEADER = Metadata.Key.of("Authorization-bin",
        Metadata.BINARY_BYTE_MARSHALLER);

    public GrpcClientHeaderInterceptor(MicroserviceIdentityHolder microserviceIdentityHolder,
                                       DownstreamConfig.Downstream downstream) {
        this.microserviceIdentityHolder = microserviceIdentityHolder;
        this.downstream = downstream;
    }

    @Override
    public <Req, Resp> ClientCall<Req, Resp> interceptCall(MethodDescriptor<Req, Resp> method,
                                                           CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<Req, Resp>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<Resp> responseListener, Metadata headers) {
                /* put custom header */
                if (downstream.isAttachToken()) {
                    headers.put(AUTHORIZATION_HEADER, String.format("Bearer %s",
                        microserviceIdentityHolder.getAccessToken()).getBytes());
                }
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<Resp>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        super.onHeaders(headers);
                    }
                }, headers);
            }
        };
    }
}
