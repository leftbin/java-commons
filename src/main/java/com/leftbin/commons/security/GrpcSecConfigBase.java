package com.leftbin.commons.security;

import com.leftbin.commons.env.exception.EnvVarMissingException;
import com.leftbin.commons.env.util.EnvUtil;
import com.leftbin.commons.proto.v1.authz.extensions.ExtensionsProto;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcServicesRegistry;
import org.lognet.springboot.grpc.security.GrpcSecurity;
import org.lognet.springboot.grpc.security.GrpcSecurityConfigurerAdapter;
import org.lognet.springboot.grpc.security.jwt.JwtAuthProviderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Configuration
@Component
@RequiredArgsConstructor
@Slf4j
public class GrpcSecConfigBase extends GrpcSecurityConfigurerAdapter {

    private static final String ENV_IDP_URL = "IDP_URL";
    private static final String ENV_ENV = "ENV";
    private static final String ENV_IDP_AUDIENCE = "IDP_AUDIENCE";

    private final GRpcServicesRegistry gRpcServicesRegistry;

    @Bean
    JwtDecoder jwtDecoder() throws EnvVarMissingException {
        EnvUtil.ensureEnvVar(ENV_ENV);
        EnvUtil.ensureEnvVar(ENV_IDP_AUDIENCE);
        EnvUtil.ensureEnvVar(ENV_IDP_URL);
        var jwtDecoder = (NimbusJwtDecoder)
                JwtDecoders.fromOidcIssuerLocation(System.getenv(ENV_IDP_URL));
        var audienceValidator = new AudienceValidator(Collections.singletonList(System.getenv(ENV_IDP_URL)));
        var defaultWithOutIssuer = JwtValidators.createDefault();
        var withAudience = new DelegatingOAuth2TokenValidator<>(defaultWithOutIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Override
    public void configure(GrpcSecurity builder) throws Exception {
        JwtAuthenticationProvider authProvider = JwtAuthProviderFactory.forAuthorities(jwtDecoder());
        // this is to get list of methods that are not tagged as public
        // is_public is a custom method option that we have to identify if method is public or not
        // gRpcServicesRegistry is has the list of grpc services registered in the service.
        // basically these are all the services which are annotated with @GRpcService
        var methods = gRpcServicesRegistry.getBeanNameToServiceBeanMap()
            .values().stream()
            .map(BindableService::bindService)
            .map(ServerServiceDefinition::getServiceDescriptor)
            .map(ServiceDescriptor::getMethods)
            .flatMap(Collection::stream)
            .filter(methodDescriptor -> {
                var supplier = (ProtoMethodDescriptorSupplier) methodDescriptor.getSchemaDescriptor();
                return Boolean.FALSE.equals(
                    supplier.getMethodDescriptor().getOptions().getExtension(ExtensionsProto.isPublic));
            }).toList();
        // the above list of methods are being passed to the GrpcServiceAuthorizationConfigurer registry
        // so that only these methods are authenticated
        builder.authorizeRequests().methods(methods.toArray(MethodDescriptor[]::new))
            .authenticated()
            .and()
            .authenticationProvider(authProvider);
    }
}
