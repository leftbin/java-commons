package com.leftbin.commons.rpc.interceptor;

import com.leftbin.commons.rpc.downstream.DownstreamConfig;
import com.leftbin.commons.security.AuthUtil;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

/**
 * AN interceptor to handle client identity header.
 */
@Slf4j
public class GrpcClientHeaderInterceptor implements ClientInterceptor {
    static final Metadata.Key<byte[]> AUTHORIZATION_HEADER = Metadata.Key.of("Authorization-bin",
        Metadata.BINARY_BYTE_MARSHALLER);
    private final DownstreamConfig.DownstreamDetails downstreamDetails;

    public GrpcClientHeaderInterceptor(DownstreamConfig.DownstreamDetails downstreamDetails) {
        this.downstreamDetails = downstreamDetails;
    }

    @Override
    public <Req, Resp> ClientCall<Req, Resp> interceptCall(MethodDescriptor<Req, Resp> method,
                                                           CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<Req, Resp>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<Resp> responseListener, Metadata headers) {
                /* put custom header */
                if (downstreamDetails.isAttachToken()) {
                    headers.put(AUTHORIZATION_HEADER, String.format("Bearer %s", AuthUtil.getMicroserviceIdentityAccessToken()).getBytes());
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
