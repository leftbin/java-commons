package com.leftbin.commons.lib.rpc.request.context;

import com.leftbin.commons.lib.rpc.security.authentication.token.AuthenticationTokenParser;
import com.leftbin.commons.proto.v1.rpc.security.authentication.extensions.ExtensionsProto;
import io.grpc.Context;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Initializes the request context using the provided GRPC context, method descriptor supplier, and authentication.
 * <p>
 * The context is created based on the provided GRPC context and then further populated based on
 * the method descriptor supplier's configuration, and the authentication object.
 * <p>
 * If the method is marked as public in the descriptor, a context with basic configuration is returned.
 * Otherwise, the context is enriched with additional details from the authentication object.
 */
@Component
@RequiredArgsConstructor
public class RequestAuthContextInitializer {
    private final AuthenticationTokenParser authenticationTokenParser;

    /**
     * Initializes a RequestContext based on the given GRPC context, method descriptor supplier, and authentication object.
     *
     * @param previousContext          the previous GRPC context
     * @param methodDescriptorSupplier the supplier for the proto method descriptor
     * @param authentication           the authentication object, possibly containing the user's credentials
     * @return a newly initialized RequestContext
     */
    public RequestAuthContext initialize(Context previousContext, ProtoMethodDescriptorSupplier methodDescriptorSupplier,
                                         Authentication authentication) {

        var isPublic = methodDescriptorSupplier.getMethodDescriptor()
            .getOptions().getExtension(ExtensionsProto.isPublic);

        var methodFullName = methodDescriptorSupplier.getMethodDescriptor().getFullName();

        var builder = RequestAuthContext.newBuilder(previousContext)
            .methodFullName(methodFullName);

        if (Boolean.TRUE.equals(isPublic)) {
            return builder.build();
        }

        return builder.requestIdentityId(authenticationTokenParser.parseId(authentication))
            .requestAccessToken(authenticationTokenParser.parseToken(authentication))
            .build();
    }
}
