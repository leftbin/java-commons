package com.leftbin.commons.lib.rpc.request;

import com.leftbin.commons.lib.rpc.request.exception.RoutingException;
import com.leftbin.commons.lib.rpc.request.routing.RequestRoute;
import com.leftbin.commons.lib.rpc.request.routing.RequestRoutes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestFactory {
    private Map<String, Request> requestBeans = new HashMap<>();

    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        var requestRouteBeans = applicationContext.getBeansWithAnnotation(RequestRoute.class);
        var requestRoutesBeans = applicationContext.getBeansWithAnnotation(RequestRoutes.class);

        // Combine beans from RequestRoute and RequestRoutes into a single map
        requestRouteBeans.putAll(requestRoutesBeans);

        // Process each bean
        for (var bean : requestRouteBeans.values()) {
            try {
                Class<?> beanClass = bean.getClass();

                // Check if the bean has a single RequestRoute annotation
                var singleMapping = beanClass.getAnnotation(RequestRoute.class);
                if (singleMapping != null) {
                    processRequestMapping(singleMapping, bean);
                }

                // Check if the bean has multiple RequestRoute annotations
                var multipleMappings = beanClass.getAnnotation(RequestRoutes.class);
                if (multipleMappings != null) {
                    for (RequestRoute mapping : multipleMappings.value()) {
                        processRequestMapping(mapping, bean);
                    }
                }
            } catch (Exception e) {
                log.error("failed to process {} bean with error: {}", bean.getClass().getName(), e.getMessage());
                e.printStackTrace();
            }
        }
        log.info("successfully mapped {} unary & streaming grpc request routes to beans", requestBeans.size());
    }

    private void processRequestMapping(RequestRoute mapping, Object bean) throws RoutingException {
        switch (mapping.type()) {
            case UNARY, SERVER_STREAMING -> {
                var fullMethodName = getFullMethodName(bean, mapping);
                requestBeans.put(fullMethodName, (Request) bean);
                log.info("handler mapped: {} -> {}", fullMethodName, bean.getClass().getName());
            }
        }
    }

    private String getFullMethodName(Object bean, RequestRoute mapping) throws RoutingException {
        Class<?> controllerClass = mapping.controller();

        String serviceName;

        try {
            serviceName = (String) controllerClass.getField("SERVICE_NAME").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            var msg = String.format("invalid request route mapping for %s class. controller class %s should be a gRPC service " +
                "class with a SERVICE_NAME field", bean.getClass().getName(), controllerClass.getName());
            throw new RoutingException(msg, e);
        }
        return String.format("%s.%s", serviceName, mapping.method());
    }

    public Request get(String fullMethodName) {
        return requestBeans.get(fullMethodName);
    }
}
