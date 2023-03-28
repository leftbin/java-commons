package com.lbn.commons.security.fga.middleware;

import com.lbn.commons.security.fga.exception.Auth0FgaApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public interface Auth0FgaMiddleware {

    // post returns response only when the api call is successful and exception in all others
    // warning: calling resp.body().string() will result in context closed, when called the second time
    Response post(String url, String moduleSecret, String jsonBody) throws IOException, Auth0FgaApiException;

    @Slf4j

    @Service
    class Auth0FgaMiddlewareImpl implements Auth0FgaMiddleware {
        // post returns response only when the api call is successful and exception in all others
        // warning: calling resp.body().string() will result in context closed, when called the second time
        @Override
        public Response post(String url, String apiKey, String jsonBody) throws IOException, Auth0FgaApiException {
            var headers = Collections.singletonMap("Content-Type", "application/json");
            var body = RequestBody.create(jsonBody.getBytes(StandardCharsets.UTF_8));
            return post(url, apiKey, body, headers);
        }

        // post returns response only when the api call is successful and exception in all others
        // warning: calling resp.body().string() will result in context closed, when called the second time
        private Response post(String url, String apiKey, RequestBody body, Map<String, String> headers) throws IOException, Auth0FgaApiException {
            log.info("sending post request to auth0 fga using {} url", url);
            var client = getHttpClient();
            var builder = new Request.Builder();
            addAuthorization(builder, apiKey);
            headers.forEach(builder::addHeader);
            var apiReq = builder.url(url)
                    .post(body)
                    .build();
            Call call = client.newCall(apiReq);
            Response resp = call.execute();
            if (isInvalidReq(resp.code())) {
                log.error("received invalid req resp for post request to auth0 fga using {} url", url);
                return resp;
            }
            if (isBadStatus(resp.code())) {
                log.error("received bad resp for post request to auth0 fga using {} url", url);
                throw new Auth0FgaApiException(getExceptionMsg(resp));
            }
            log.info("received good response from auth0 fga for post req to {} url", url);
            return resp;
        }

        private OkHttpClient getHttpClient() {
            return new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
        }

        private void addAuthorization(Request.Builder builder, String apiKey) {
            builder.addHeader("Authorization", String.format("Bearer %s", apiKey));
        }

        public static String getExceptionMsg(Response resp) throws IOException {
            var msg = String.format("bad resp code from auth0 fga: %d.", resp.code());
            if (resp.body() != null) {
                msg = String.format("%s. body: %s", msg, resp.body().string());
            }
            return msg;
        }

        private boolean isInvalidReq(int c) {
            return c == HttpStatus.SC_BAD_REQUEST;
        }

        private boolean isBadStatus(int c) {
            return c != HttpStatus.SC_OK && c != HttpStatus.SC_CREATED && c != HttpStatus.SC_ACCEPTED;
        }
    }

}
