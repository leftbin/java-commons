package com.leftbin.commons.lib.rpc.security;

import com.leftbin.commons.lib.rpc.security.authentication.config.AuthenticationConfig;
import com.leftbin.commons.lib.rpc.security.authentication.token.JwtTokenAudienceValidator;
import com.leftbin.commons.proto.v1.rpc.security.authentication.extensions.ExtensionsProto;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.ServiceDescriptor;
import io.grpc.protobuf.ProtoMethodDescriptorSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcServicesRegistry;
import org.lognet.springboot.grpc.autoconfigure.OnGrpcServerEnabled;
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

/**
 * This class configures gRPC security settings for the service. It sets up JWT decoding
 * and configures gRPC authorization rules for methods.
 */
@Configuration
@Component
@RequiredArgsConstructor
@Slf4j
@OnGrpcServerEnabled
public class GrpcSecurityConfigBase extends GrpcSecurityConfigurerAdapter {
    private final GRpcServicesRegistry gRpcServicesRegistry;
    private final AuthenticationConfig authenticationConfig;

    /**
     * Provides a bean for decoding JWTs. Sets up the decoder with default validations
     * and a custom audience validator.
     *
     * @return the JwtDecoder instance.
     */
    @Bean
    JwtDecoder jwtDecoder() {
        var jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(authenticationConfig.getIdpUrl());
        var jwtTokenAudienceValidator = JwtTokenAudienceValidator.builder()
            .audienceList(Collections.singletonList(authenticationConfig.getApiAudience()))
            .build();
        var withAudience = new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(), jwtTokenAudienceValidator);
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }

    /**
     * Configures gRPC server to use JWT authentication and sets the authorization rules.
     * The rules require that non-public methods must be authenticated.
     *
     * @param builder the GrpcSecurity instance to configure.
     * @throws Exception if there's an error during the configuration process.
     */
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
