package com.leftbin.commons.lib.rpc.request.routing;

import com.leftbin.commons.lib.rpc.request.method.RequestMethod;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequestRoutes.class)
public @interface RequestRoute {
    Class<?> controller();

    String method();

    RequestMethod.Type type() default RequestMethod.Type.UNARY;
}
