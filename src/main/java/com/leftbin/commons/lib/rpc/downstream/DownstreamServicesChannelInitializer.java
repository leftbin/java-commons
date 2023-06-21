package com.leftbin.commons.lib.rpc.downstream;

import com.leftbin.commons.lib.rpc.downstream.config.DownstreamConfig;
import com.leftbin.commons.lib.rpc.downstream.interceptor.GrpcClientHeaderInterceptor;
import com.leftbin.commons.lib.rpc.security.authentication.microservice.MicroserviceIdentityHolder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * This class is used to initialize the gRPC channels for each of the downstream services configured in the application.
 * <p>
 * It listens to the ApplicationReadyEvent to create the gRPC channels after all the configurations have been loaded and the application is ready to serve requests.
 * <p>
 * The ManagedChannel for each downstream service is created based on the configuration of the service from DownstreamConfig.
 * The gRPC channel can be created with transport level security or without it, depending on the endpoint.
 */
@Component
@Slf4j
public class DownstreamServicesChannelInitializer {
    private final DownstreamConfig downstreamConfig;
    private final MicroserviceIdentityHolder microserviceIdentityHolder;
    private static final String DefaultSecurePort = "443";
    private static final Integer MAX_INBOUND_METADATA_SIZE = 1000 * 1024; //10 MB

    public DownstreamServicesChannelInitializer(DownstreamConfig downstreamConfig,
                                                MicroserviceIdentityHolder microserviceIdentityHolder) {
        this.downstreamConfig = downstreamConfig;
        this.microserviceIdentityHolder = microserviceIdentityHolder;
        downstreamConfig.getServices().values()
            .parallelStream()
            .forEach(this::createGrpcConfiguration);
    }

    /**
     * This method creates and sets a ManagedChannel for a given downstream service.
     * It decides the type of channel (secure or plaintext) based on the endpoint of the downstream service.
     *
     * @param downstream The Downstream object representing the service for which the channel needs to be created.
     */
    private void createGrpcConfiguration(DownstreamConfig.Downstream downstream) {
        var grpcClientHeaderInterceptor = new GrpcClientHeaderInterceptor(microserviceIdentityHolder, downstream);
        //when endpoint ends with default port suffix, secure channel is created else plaintext channel.
        if (downstream.getEndpoint().endsWith(DefaultSecurePort)) {
            downstream.setChannel(ManagedChannelBuilder.forTarget(downstream.getEndpoint())
                .maxInboundMetadataSize(MAX_INBOUND_METADATA_SIZE)
                .useTransportSecurity()
                .intercept(grpcClientHeaderInterceptor)
                .build());
        } else {
            downstream.setChannel(ManagedChannelBuilder.forTarget(downstream.getEndpoint())
                .intercept(grpcClientHeaderInterceptor)
                .usePlaintext()
                .build());
        }
    }

    /**
     * This method is called when the application context is being destroyed. It shuts down the ManagedChannel
     * for all the downstream services to release resources.
     */
    @PreDestroy
    private void cleanUp() {
        log.debug("shutting down grpc managed channels for all services");
        downstreamConfig.getServices().values().parallelStream().map(DownstreamConfig.Downstream::getChannel)
            .forEach(ManagedChannel::shutdown);
    }
}
