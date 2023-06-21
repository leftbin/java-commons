package com.leftbin.commons.lib.rpc.downstream.config;

import io.grpc.ManagedChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to configure the downstream services from the properties defined in the application.yaml file.
 * This configuration includes the endpoint, the communication channel and whether or not to attach a token to requests to the service.
 *
 * It uses Spring's @ConfigurationProperties annotation to map the properties with prefix 'downstream' into a Map of Downstream objects.
 */
@Configuration
@ConfigurationProperties(prefix = "downstream")
@Slf4j
@Data
@Lazy
@NoArgsConstructor
public class DownstreamConfig {
    private Map<String, Downstream> services = new HashMap<>();
    @Data
    public static class Downstream {
        private String endpoint;
        private ManagedChannel channel;
        private boolean attachToken;
    }

    /**
     * This field represents a Function which maps a String (representing the service name) to a Downstream object.
     *
     * It's implemented using a method reference (services::get) to the `get` method on the `services` HashMap.
     * This means that when the function is invoked with a service name, it will return the corresponding Downstream object
     * from the `services` HashMap, or null if no such service is found.
     *
     * This is essentially a shorthand for looking up service configurations by their name. By storing this function as a field,
     * it can be easily reused throughout the codebase whenever we need to fetch a service's configuration.
     *
     * For example, `SERVICE_CONFIGURATION.apply("stack")` would return the Downstream object for the 'stack' service.
     */
    public final Function<String, Downstream> SERVICE_CONFIGURATION = services::get;
}
