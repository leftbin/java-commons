package com.leftbin.commons.lib.rpc.security.authentication.token;

import lombok.Builder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.List;

/**
 * Validates that the JWT token contains the intended audience in its claims.
 */
@Builder
public class JwtTokenAudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final List<String> audienceList;

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        OAuth2Error error = new OAuth2Error("invalid_token", String.format("The required audience %s is missing", this.audienceList), null);

        if (!Collections.disjoint(jwt.getAudience(), audienceList)) {
            return OAuth2TokenValidatorResult.success();
        }

        return OAuth2TokenValidatorResult.failure(error);
    }
}
