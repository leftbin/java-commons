package com.leftbin.commons.lib.rpc.request.context;

import io.grpc.Context;

/**
 * RequestAuthContext holds the context for a request and provides getter and builder methods to work with the request context.
 */
public class RequestAuthContext {
    private Context previousContext;
    private static final Context.Key<String> METHOD_FULL_NAME_KEY = Context.key("METHOD_FULL_NAME");
    private static final Context.Key<String> REQUEST_ACCESS_TOKEN_KEY = Context.key("REQUEST_ACCESS_TOKEN");
    private static final Context.Key<String> REQUEST_IDENTITY_ID_KEY = Context.key("REQUEST_IDENTITY_ID");

    public static Builder newBuilder(Context previousContext) {
        return new Builder().previousContext(previousContext);
    }

    public void setPreviousContext(Context previousContext) {
        this.previousContext = previousContext;
    }

    public Context getPreviousContext() {
        return previousContext;
    }

    public static String getMethodFullName() {
        return METHOD_FULL_NAME_KEY.get(Context.current());
    }

    /**
     * Gets the request access token from the context.
     *
     * @return the request access token
     */
    public static String getRequestAccessToken() {
        return REQUEST_ACCESS_TOKEN_KEY.get(Context.current());
    }

    /**
     * Gets the request identity ID from the context.
     *
     * @return the request identity ID
     */
    public static String getRequestIdentityId() {
        return REQUEST_IDENTITY_ID_KEY.get(Context.current());
    }

    public static class Builder {
        private Context previousContext;

        public Builder previousContext(Context previousContext) {
            this.previousContext = previousContext;
            return this;
        }

        public Builder methodFullName(String methodFullName) {
            this.previousContext = this.previousContext.withValue(METHOD_FULL_NAME_KEY,
                methodFullName);
            return this;
        }

        public Builder requestAccessToken(String requestAccessToken) {
            this.previousContext = this.previousContext.withValue(REQUEST_ACCESS_TOKEN_KEY,
                requestAccessToken);
            return this;
        }

        public Builder requestIdentityId(String requestIdentityId) {
            this.previousContext = this.previousContext.withValue(REQUEST_IDENTITY_ID_KEY,
                requestIdentityId);
            return this;
        }

        public RequestAuthContext build() {
            var requestContext = new RequestAuthContext();
            requestContext.setPreviousContext(this.previousContext);
            return requestContext;
        }
    }
}
