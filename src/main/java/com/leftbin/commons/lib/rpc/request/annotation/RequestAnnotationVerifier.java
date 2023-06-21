package com.leftbin.commons.lib.rpc.request.annotation;

import com.leftbin.commons.lib.rpc.request.Request;

import java.lang.annotation.Annotation;

public class RequestAnnotationVerifier {
    /**
     * Checks if the given request object's class has the specified annotation.
     *
     * @param request          The request object.
     * @param annotationClass  The class of the annotation to look for.
     * @return                 True if the annotation is present, false otherwise.
     */
    public static boolean verify(Request<?, ?> request, Class<? extends Annotation> annotationClass) {
        if (request == null || annotationClass == null) {
            return false;
        }

        return request.getClass().isAnnotationPresent(annotationClass);
    }
}
