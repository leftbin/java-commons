package com.lbn.commons.security;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.lbn.commons.env.exception.EnvVarMissingException;
import com.lbn.commons.env.util.EnvUtil;
import com.lbn.commons.security.exception.MicroserviceIdentityException;
import io.grpc.Metadata;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Map;

@Slf4j
public class AuthUtil {

    private static final long TOKEN_EXPIRY_MINUTES_LIMIT = 10;
    private static TokenHolder microserviceIdentityTokenHolder = null;
    private static final String ENV_ENV = "ENV";
    private static final String ENV_IDP_URL = "IDP_URL";
    private static final String ENV_IDP_DOMAIN = "IDP_DOMAIN";
    private static final String ENV_MICROSERVICE_IDENTITY_IDP_CLIENT_ID = "MICROSERVICE_IDENTITY_IDP_CLIENT_ID";
    private static final String ENV_MICROSERVICE_IDENTITY_IDP_CLIENT_SECRET = "MICROSERVICE_IDENTITY_IDP_CLIENT_SECRET";
    private static final String IDP_API_AUDIENCE = "https://api.planton.cloud/";

    private static final String IDP_TOKEN_CUSTOM_CLAIM_EMAIL_KEY = "https://planton.cloud/email";

    public static String getMicroserviceMachineAccountId() {
        Assert.notNull(microserviceIdentityTokenHolder, "microservice identity token holder can not be null");
        var decodedJWT = JWT.decode(microserviceIdentityTokenHolder.getAccessToken());
        return decodedJWT.getSubject();
    }

    public static String getMicroserviceMachineAccountEmail() {
        Assert.notNull(microserviceIdentityTokenHolder, "microservice identity token holder can not be null");
        var decodedJWT = JWT.decode(microserviceIdentityTokenHolder.getAccessToken());
        for (Map.Entry<String, Claim> entry : decodedJWT.getClaims().entrySet()) {
            if (entry.getKey().equals(IDP_TOKEN_CUSTOM_CLAIM_EMAIL_KEY)) {
                return entry.getValue().asString();
            }
        }
        return "";
    }

    public static String getUserAccountId(Authentication auth) {
        var tokenHolder = (JwtAuthenticationToken) auth;
        return tokenHolder.getToken().getSubject();
    }

    public static String getToken(Authentication auth) {
        var tokenHolder = (JwtAuthenticationToken) auth;
        return tokenHolder.getToken().getTokenValue();
    }

    public static String getEmail(Authentication auth) {
        var tokenHolder = (JwtAuthenticationToken) auth;
        for (Map.Entry<String, Object> entry : tokenHolder.getToken().getClaims().entrySet()) {
            if (entry.getKey().equals(IDP_TOKEN_CUSTOM_CLAIM_EMAIL_KEY)) {
                return entry.getValue().toString();
            }
        }
        return "";
    }

    //initializeMicroserviceIdentity exchanges the client credentials for an oauth token from the identity provider and
    // stores the retrieved token for future requests.
    public static void initializeMicroserviceIdentity() throws MicroserviceIdentityException {
        log.info("initializing service identity");
        try {
            microserviceIdentityTokenHolder = getMicroserviceToken();
        } catch (EnvVarMissingException e) {
            throw new MicroserviceIdentityException(e.getMessage());
        } catch (Auth0Exception e) {
            throw new MicroserviceIdentityException(String.format("failed to fetch token from auth0 with error: %s", e.getMessage()));
        }
        log.info("successfully initialized microservice identity");
    }

    public static TokenHolder getMicroserviceToken() throws EnvVarMissingException, Auth0Exception {
        log.info("fetching token from idp");
        EnvUtil.ensureEnvVar(ENV_ENV);
        EnvUtil.ensureEnvVar(ENV_IDP_URL);
        EnvUtil.ensureEnvVar(ENV_IDP_DOMAIN);
        EnvUtil.ensureEnvVar(ENV_MICROSERVICE_IDENTITY_IDP_CLIENT_ID);
        EnvUtil.ensureEnvVar(ENV_MICROSERVICE_IDENTITY_IDP_CLIENT_SECRET);
        var userAuthApi = new AuthAPI(System.getenv(ENV_IDP_DOMAIN), System.getenv(ENV_MICROSERVICE_IDENTITY_IDP_CLIENT_ID), System.getenv(ENV_MICROSERVICE_IDENTITY_IDP_CLIENT_SECRET));
        var tokenHolder = userAuthApi.requestToken(IDP_API_AUDIENCE).execute();
        log.info("successfully fetched token from idp");
        return tokenHolder;
    }

    // attach returns the stub provided in the input after attaching the cached service identity token as the
    // Authorization header in the request metadata.
    public static <T extends AbstractStub<T>> T attach(T stub) {
        return MetadataUtils.attachHeaders(stub, getMetadataWithToken(microserviceIdentityTokenHolder.getAccessToken()));
    }

    private static Metadata getMetadataWithToken(String token) {
        Metadata authMetadata = new Metadata();
        Metadata.Key<byte[]> idHeader = Metadata.Key.of("Authorization-bin", Metadata.BINARY_BYTE_MARSHALLER);
        authMetadata.put(idHeader, String.format("Bearer %s", token).getBytes());
        return authMetadata;
    }

    // rotateToken checks for the expiration on the cached jwt token and
    // if the number of minutes left before the token expires is greater than the limit,
    // the token is rotated and the cached value is replaced with the new token.
    public static void rotateToken() throws EnvVarMissingException, Auth0Exception {
        if (!isTokenExpiring(microserviceIdentityTokenHolder.getAccessToken(), TOKEN_EXPIRY_MINUTES_LIMIT)) {
            return;
        }
        log.info("microservice machine account token is about to expire in less than {} minutes... " +
            "rotating to fetch new token.", TOKEN_EXPIRY_MINUTES_LIMIT);
        microserviceIdentityTokenHolder = getMicroserviceToken();
    }

    //confirms if the provided token is expiring in the provided number of minutes
    public static boolean isTokenExpiring(String accessToken, long minutes) {
        var decodedJWT = JWT.decode(accessToken);
        var timeDiff = decodedJWT.getExpiresAt().getTime() - new java.util.Date().getTime();
        long diffInMinutes = (timeDiff / (1000 * 60)) % 60;
        long diffInHours = (timeDiff / (1000 * 60 * 60)) % 24;
        long diffInDays = (timeDiff / (1000 * 60 * 60 * 24)) % 365;
        return diffInDays == 0 && diffInHours == 0 && diffInMinutes < minutes;
    }

    public static String getMicroserviceIdentityAccessToken() {
        return microserviceIdentityTokenHolder.getAccessToken();
    }
}
