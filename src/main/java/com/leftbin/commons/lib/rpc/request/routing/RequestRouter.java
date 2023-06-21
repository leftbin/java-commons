package com.leftbin.commons.lib.rpc.request.routing;

import com.google.protobuf.Message;
import com.leftbin.commons.lib.rpc.request.RequestFactory;
import com.leftbin.commons.lib.rpc.request.RequestHandler;
import com.leftbin.commons.lib.rpc.request.context.RequestAuthContext;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestRouter {
    private final RequestFactory requestFactory;

    public <I extends Message, O extends Message> void route(I input, StreamObserver<O> responseObserver) {
        var requestBean = requestFactory.get(RequestAuthContext.getMethodFullName());
        if (Objects.isNull(requestBean)) {
            log.error("request mapping not found for {}", RequestAuthContext.getMethodFullName());
            responseObserver.onError(Status.INTERNAL.withDescription("routable mapping not found").asException());
            return;
        }
        var handler = new RequestHandler<I,O>(requestBean);
        handler.handle(input, responseObserver);
    }
}
