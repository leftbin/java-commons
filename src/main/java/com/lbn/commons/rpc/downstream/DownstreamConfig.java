package com.lbn.commons.rpc.downstream;

import com.lbn.commons.rpc.interceptor.GrpcClientHeaderInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
@ConfigurationProperties(prefix = "downstream")
@Slf4j
@Data
@Lazy
public class DownstreamConfig {

    private static final String DefaultSecurePort = "443";

    @Data
    public static class DownstreamDetails {
        private String endpoint;
        private ManagedChannel channel;
        private boolean attachToken;
    }

    private Map<String, DownstreamDetails> services = new HashMap<>();
    public final Function<String, DownstreamDetails> SERVICE_CONFIGURATION = services::get;

    @PostConstruct
    private void init() {
        services.values()
                .parallelStream()
                .forEach(this::createGrpcConfiguration);
    }

    private void createGrpcConfiguration(DownstreamDetails configuration) {
        GrpcClientHeaderInterceptor grpcClientHeaderInterceptor =
            new GrpcClientHeaderInterceptor(configuration);
        //when endpoint ends with default port suffix, secure channel is created else plaintext channel.
        if (configuration.endpoint.endsWith(":443")) {
            configuration.channel = ManagedChannelBuilder.forTarget(configuration.endpoint)
                .useTransportSecurity()
                .intercept(grpcClientHeaderInterceptor)
                .build();
        } else {
            configuration.channel = ManagedChannelBuilder.forTarget(configuration.endpoint)
                .intercept(grpcClientHeaderInterceptor)
                .usePlaintext()
                .build();
        }
    }

    @PreDestroy
    private void cleanUp() {
        log.debug("shutting down grpc managed channels for all services");
        services.values().parallelStream().map(DownstreamDetails::getChannel)
                .forEach(ManagedChannel::shutdown);
    }
}
