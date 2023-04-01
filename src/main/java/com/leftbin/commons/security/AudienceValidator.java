package com.leftbin.commons.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.List;

/**
 * Validates that the JWT token contains the intended audience in its claims.
 */
class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final List<String> audienceList;

    AudienceValidator(List<String> audienceList) {
        this.audienceList = audienceList;
    }

    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        OAuth2Error error = new OAuth2Error("invalid_token", String.format("The required audience %s is missing", this.audienceList), null);

        if (!Collections.disjoint(jwt.getAudience(), audienceList)) {
            return OAuth2TokenValidatorResult.success();
        }

        return OAuth2TokenValidatorResult.failure(error);
    }
}
