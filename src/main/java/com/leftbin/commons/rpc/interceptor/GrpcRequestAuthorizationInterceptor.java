package com.leftbin.commons.rpc.interceptor;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.leftbin.commons.proto.v1.authz.extensions.ExtensionsProto;
import com.leftbin.commons.security.AuthUtil;
import io.grpc.*;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.leftbin.commons.rpc.keys.RpcContextKeys.*;
import static com.leftbin.commons.rpc.keys.RpcMetadataKeys.X_REQUEST_ID;

@Slf4j
@GRpcGlobalInterceptor
public class GrpcRequestAuthorizationInterceptor implements ServerInterceptor {
    @Override
    public <Req, Resp> ServerCall.Listener<Req> interceptCall(ServerCall<Req, Resp> call,
                                                              Metadata headers,
                                                              ServerCallHandler<Req, Resp> next) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        GrpcServerCall<Req, Resp> grpcServerCall = new GrpcServerCall<>(call);


        var methodDescriptorSupplier = (ProtoMethodDescriptorSupplier) grpcServerCall.getMethodDescriptor().getSchemaDescriptor();
        var authorizationConfig = methodDescriptorSupplier.getMethodDescriptor()
            .getOptions().getExtension(ExtensionsProto.authorizationConfig);
        var isPublic = methodDescriptorSupplier.getMethodDescriptor()
            .getOptions().getExtension(ExtensionsProto.isPublic);
        Context context = Context.current()
            .withValue(MICROSERVICE_MACHINE_ACCOUNT_ID, AuthUtil.getMicroserviceMachineAccountId())
            .withValue(AUTHORIZATION_CONFIG, authorizationConfig)
            .withValue(REQUEST_ID, headers.get(X_REQUEST_ID));
        if (Boolean.FALSE.equals(isPublic)) {
            context = context.withValue(USER_ACCOUNT_ID, AuthUtil.getUserAccountId(authentication))
                .withValue(EMAIL, AuthUtil.getEmail(authentication))
                .withValue(ACCESS_TOKEN, AuthUtil.getToken(authentication));
        }

        // Obtain a listener attached to the current Context (Needed to propagate current context)
        ServerCall.Listener<Req> listener = Contexts.interceptCall(context, grpcServerCall, headers, next);

        return new GrpcForwardingServerCallListener<>(call.getMethodDescriptor(), listener) {
            @Override
            public void onMessage(Req message) {
//            TODO - Need to discuss with team before enabling the rpc request log
//                log.info("grpc request - method: {}, headers : {}, message: {}", methodName, headers,
//                        printProtoAsJson((MessageOrBuilder) message));
                super.onMessage(message);
            }
        };
    }

    private static class GrpcServerCall<Req, Resp> extends ServerCall<Req, Resp> {

        ServerCall<Req, Resp> serverCall;

        protected GrpcServerCall(ServerCall<Req, Resp> serverCall) {
            this.serverCall = serverCall;
        }

        @Override
        public void request(int numMessages) {
            serverCall.request(numMessages);
        }

        @Override
        public void sendHeaders(Metadata headers) {
            serverCall.sendHeaders(headers);
        }

        @Override
        public void sendMessage(Resp message) {
//            TODO - Need to discuss with the team before enabling the rpc response log
//            log.info("grpc response - method: {} response: {}",
//                    serverCall.getMethodDescriptor().getFullMethodName(),
//                    printProtoAsJson((MessageOrBuilder) message));
            serverCall.sendMessage(message);
        }

        @Override
        public void close(Status status, Metadata trailers) {
            serverCall.close(status, trailers);
        }

        @Override
        public boolean isCancelled() {
            return serverCall.isCancelled();
        }

        @Override
        public MethodDescriptor<Req, Resp> getMethodDescriptor() {
            return serverCall.getMethodDescriptor();
        }
    }

    private static class GrpcForwardingServerCallListener<Req>
        extends ForwardingServerCallListener.SimpleForwardingServerCallListener<Req> {

        String methodName;

        protected GrpcForwardingServerCallListener(MethodDescriptor method, ServerCall.Listener<Req> listener) {
            super(listener);
            methodName = method.getFullMethodName();
        }
    }

    private static String printProtoAsJson(MessageOrBuilder messageV3) {
        try {
            return JsonFormat.printer().print(messageV3);
        } catch (InvalidProtocolBufferException e) {
            return messageV3.toString();
        }
    }
}
